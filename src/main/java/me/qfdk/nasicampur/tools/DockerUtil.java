package me.qfdk.nasicampur.tools;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;

public class DockerUtil {

    private static DockerClient instance = null;

    private DockerUtil() {
    }

    public static DockerClient getInstance() {
        if (instance == null)
            instance = new DefaultDockerClient("unix:///var/run/docker.sock");

        return instance;
    }
}
