package me.qfdk.nasicampur.service;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DockerService {

    private DockerClient docker;

    private final static String SSR_IMAGE_NAME = "breakwa11/shadowsocksr:latest";

    public DockerService() {
        docker = new DefaultDockerClient("unix:///var/run/docker.sock");
    }

    public String createContainer(String passwd, String port, String wechatName) {
        downloadImage(SSR_IMAGE_NAME);
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        List<PortBinding> randomPort = new ArrayList<>();
        randomPort.add(PortBinding.of("0.0.0.0", port));
        portBindings.put("51348/tcp", randomPort);

        HostConfig hostConfig = HostConfig.builder()
                .publishAllPorts(true)
                .portBindings(portBindings)
                .restartPolicy(HostConfig.RestartPolicy.always())
                .build();
        // Create container with exposed ports
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image("breakwa11/shadowsocksr:latest")
                .exposedPorts("51348/tcp")
                .cmd("sh", "-c", "python server.py -p 51348 -k " + passwd + " -m rc4-md5 -O origin -o plain")
                .build();
        ContainerCreation creation = null;
        try {
            creation = docker.createContainer(containerConfig, wechatName);
        } catch (DockerException e) {
            System.err.println("Docker 服务没有启动或者升级pom中docker 版本.");
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String id = creation.id();
        return id;
    }

    public int startContainer(String id) {
        try {
            docker.startContainer(id);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    public int restartContainer(String id) {
        try {
            docker.restartContainer(id);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    public int stopContainer(String id) {
        try {
            docker.stopContainer(id, 0);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    public int deleteContainer(String id) {
        try {
            docker.removeContainer(id);
            return 1;
        } catch (Exception e) {
            return -1;
        }
    }

    public ContainerStats getContainerState(String containerId) {
        try {
            return docker.stats(containerId);
        } catch (Exception e) {
            return null;
        }
    }

    public ContainerInfo getInfoContainer(String id) {
        try {
            return docker.inspectContainer(id);
        } catch (DockerException e) {
            System.err.println("Docker 服务或没有启动.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void downloadImage(String imageName) {
        List<Image> images = null;
        try {
            images = docker.listImages();
            if (images == null) {
                System.err.println("Docker 服务或没有启动.");
            }
        } catch (DockerException e) {
            System.err.println("Docker 服务或没有启动.");
            System.exit(-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        boolean needDownload = true;

        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).repoTags().get(0).equals(imageName)) {
                needDownload = false;
            }
        }

        if (needDownload) {
            try {
                docker.pull(imageName);
            } catch (DockerException e) {
                System.err.println("Docker 服务或没有启动.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
