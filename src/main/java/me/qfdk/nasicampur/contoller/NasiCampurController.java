package me.qfdk.nasicampur.contoller;

import com.spotify.docker.client.messages.ContainerStats;
import com.spotify.docker.client.messages.NetworkStats;
import me.qfdk.nasicampur.service.DockerService;
import me.qfdk.nasicampur.tools.Outil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class NasiCampurController {

    @Value("${server.port}")
    private String port;

    @Autowired
    private DockerService dockerService;

    @GetMapping("/createContainer")
    public Map<String, String> createContainer(@RequestParam(value = "wechatName") String wechatName) {
        String pass = Outil.getPass(wechatName);
        String port = Outil.getRandomPort();
        String containerId = dockerService.createContainer(pass, port);
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

    @GetMapping("/getNetworkStats")
    public Map<String, Double> getNetworkStats(@RequestParam("id") String containerId) {
        Map<String, Double> map = new HashMap<>();
        NetworkStats traffic=dockerService.getContainerState(containerId).networks().get("eth0");
        map.put("txBytes",traffic.txBytes() / 1000000.0);
        map.put("rxBytes",traffic.rxBytes() / 1000000.0);
        return map;
    }

    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    public String sayHi(@RequestParam(value = "name", defaultValue = "forezp") String name) {
        return "Hi," + name + ", from ->" + port;
    }
}