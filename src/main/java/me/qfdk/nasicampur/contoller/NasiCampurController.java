package me.qfdk.nasicampur.contoller;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.spotify.docker.client.messages.NetworkStats;
import lombok.extern.slf4j.Slf4j;
import me.qfdk.nasicampur.service.DockerService;
import me.qfdk.nasicampur.service.PontService;
import me.qfdk.nasicampur.tools.Outil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class NasiCampurController {

    @Autowired
    private DockerService dockerService;

    @Autowired
    private PontService pontService;

    private Map<String, Session> mapSession = new HashMap<>();

    @GetMapping("/createContainer")
    public Map<String, String> createContainer(@RequestParam(value = "wechatName") String wechatName, @RequestParam(value = "port", defaultValue = "") String port) {
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
    public String addPort(@RequestParam("sshUser") String sshUser, @RequestParam("sshPassowrd") String sshPassowrd, @RequestParam("host") String host, @RequestParam("port") String port) {
        Session session = pontService.addPort(sshUser, sshPassowrd, Integer.parseInt(port), host, Integer.parseInt(port));
        mapSession.put(port, session);
        return "OK";
    }

    @RequestMapping(value = "/deletePont", method = RequestMethod.GET)
    public String deletePort(@RequestParam("port") String port) {
        try {
            mapSession.get(port).delPortForwardingL(Integer.parseInt(port));
            mapSession.remove(port);
        } catch (JSchException e) {
            e.printStackTrace();
            return "KO";
        }
        return "OK";
    }

}