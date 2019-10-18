package me.qfdk.nasicampur.controller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.spotify.docker.client.messages.NetworkStats;
import lombok.extern.slf4j.Slf4j;
import me.qfdk.nasicampur.NasiCampurApplication;
import me.qfdk.nasicampur.entity.User;
import me.qfdk.nasicampur.service.DockerService;
import me.qfdk.nasicampur.service.PontService;
import me.qfdk.nasicampur.tools.Outil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RefreshScope
public class NasiCampurController {

    @Autowired
    RestTemplate restTemplate;

    @Value("${spring.application.name}")
    String prxoyLocation;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private PontService pontService;
    private Map<String, Session> mapSession = new HashMap<>();


//    @PostConstruct
//    public void init() {
//        runProxy();
//    }

    @GetMapping("/runProxy")
    @ResponseBody
    public String runProxy() {
        log.info("[{}]: 添加中转服务器.", "Proxy");
        String pass = restTemplate.getForObject("http://nasi-mie/getSSHPassword", String.class);
        User[] users = restTemplate.getForObject("http://nasi-mie/getProxyList?location=" + prxoyLocation, User[].class);
        if (users != null && users.length > 0) {
            for (User user : users) {
                log.info("[{}]: 启动中转服务器{} => {}.", user.getWechatName(), user.getPontLocation(), user.getContainerLocation());
                Session session = pontService.addPort("root", pass, user.getContainerLocation() + ".qfdk.me", Integer.parseInt(user.getContainerPort()));
                mapSession.put(user.getContainerPort(), session);
            }
            log.info("[{}]: 添加中转服务器完成.", "Proxy");
            return "添加中转服务器 success. " + users.length;
        } else {
            log.warn("[{}]: 无需添加中转服务器.", "Proxy");
            return "无需添加中转服务器.";
        }
    }

    @GetMapping("/createContainer")
    public Map<String, String> createContainer(@RequestParam(value = "wechatName") String wechatName, @RequestParam(value = "port", defaultValue = "") String port) {
        return createContainerMap(wechatName, port);
    }

    private Map<String, String> createContainerMap(@RequestParam("wechatName") String wechatName, @RequestParam(value = "port", defaultValue = "") String port) {
        String pass = Outil.getPass(wechatName);

        if (StringUtils.isEmpty(port)) {
            port = Outil.getRandomPort();
        }

        String containerId = dockerService.createContainer(pass, port, wechatName);
        dockerService.startContainer(containerId);
        String status = dockerService.getInfoContainer(containerId).state().status();
        Map<String, String> map = new HashMap<>();
        map.put("containerId", containerId);
        map.put("status", status);
        map.put("pass", pass);
        map.put("port", port);
        return map;
    }

    @GetMapping("/deleteContainer")
    public int deleteContainer(@RequestParam("id") String containerId) {
        dockerService.stopContainer(containerId);
        return dockerService.deleteContainer(containerId);
    }

    @GetMapping("/startContainer")
    public String startContainer(@RequestParam("id") String containerId) {
        dockerService.startContainer(containerId);
        return dockerService.getInfoContainer(containerId).state().status();
    }

    @GetMapping("/restartContainer")
    public String restartContainer(@RequestParam("id") String containerId) {
        dockerService.restartContainer(containerId);
        return dockerService.getInfoContainer(containerId).state().status();
    }

    @GetMapping("/stopContainer")
    public String stopContainer(@RequestParam("id") String containerId) {
        dockerService.stopContainer(containerId);
        return dockerService.getInfoContainer(containerId).state().status();
    }

    @GetMapping("/reCreateContainer")
    public Map<String, String> reCreateContainer(@RequestParam(value = "wechatName") String wechatName, @RequestParam(value = "port", defaultValue = "") String port) {
        dockerService.stopContainer(wechatName);
        dockerService.deleteContainer(wechatName);
        return createContainerMap(wechatName, port);
    }

    @GetMapping("/info")
    public String info(@RequestParam("id") String containerId) {
        return dockerService.getInfoContainer(containerId).state().status();
    }

    @GetMapping("/containerCount")
    public int containerCount() {
        return dockerService.containerCount();
    }


    @GetMapping("/getNetworkStats")
    @Async
    public Map<String, Double> getNetworkStats(@RequestParam("id") String containerId) {
        Map<String, Double> map = new HashMap<>();
        NetworkStats traffic = dockerService.getContainerState(containerId).networks().get("eth0");
        map.put("txBytes", traffic.txBytes() / 1000000.0);
        map.put("rxBytes", traffic.rxBytes() / 1000000.0);
        log.info(String.valueOf(map));
        return map;
    }

    @RequestMapping(value = "/addPont", method = RequestMethod.GET)
    public String addPort(@RequestParam("sshUser") String sshUser, @RequestParam("sshPassword") String sshPassword, @RequestParam("host") String host, @RequestParam("port") String port) {
        Session session = pontService.addPort(sshUser, sshPassword, host, Integer.parseInt(port));
        mapSession.put(port, session);
        return "OK";
    }

    @RequestMapping(value = "/deletePont", method = RequestMethod.GET)
    public String deletePort(@RequestParam("port") String port) {
        try {
            if (mapSession.get(port) != null) {
                mapSession.get(port).delPortForwardingL(Integer.parseInt(port));
                mapSession.get(port).disconnect();
                mapSession.remove(port);
                log.info("[删除端口转发] -> {}", port);
            }
        } catch (JSchException e) {
            e.printStackTrace();
            return "KO";
        }
        return "OK";
    }

    @RequestMapping(value = "/updateIp", method = RequestMethod.GET)
    public void updateIp(@RequestParam("ip") String ip) {
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        dumperOptions.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        dumperOptions.setPrettyFlow(false);
        Yaml yaml = new Yaml(dumperOptions);
        Map map = null;
        try {
            map = (Map) yaml.load(new FileInputStream(new File(System.getProperty("user.dir") + "/application.yaml")));
        } catch (FileNotFoundException e) {
            log.error("application.yaml 未找到");
        }
        System.out.println("Old ip => " + ((Map) ((Map) map.get("nasi")).get("campur")).get("ip"));
        ((Map) ((Map) map.get("nasi")).get("campur")).put("ip", ip);
        try {
            yaml.dump(map, new OutputStreamWriter(new FileOutputStream(new File(System.getProperty("user.dir") + "/application.yaml"))));
        } catch (FileNotFoundException e) {
            log.error("application.yaml 未找到");
        }

        ExecutorService threadPool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardOldestPolicy());
        threadPool.execute(() -> {
            NasiCampurApplication.context.close();
            log.info("准备重启服务器完成新IP地址注册...{}", ip);
            NasiCampurApplication.context = SpringApplication.run(NasiCampurApplication.class, "");
            log.info("完成重新注册...{}", ip);
        });
        threadPool.shutdown();
    }

}