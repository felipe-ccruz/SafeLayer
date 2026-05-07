package com.felp.backvm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DockerService {

    private static final Logger log = LoggerFactory.getLogger(DockerService.class);
    private static final String IMAGE = "ubuntu";
    private static final int TIMEOUT_SECONDS = 30;

    public String containerName(Long vmId) {
        return "simplevm-" + vmId;
    }

    public void createContainer(Long vmId) {
        String name = containerName(vmId);
        runOptional("docker", "rm", "-f", name);
        run("docker", "create", "--name", name, IMAGE, "sleep", "infinity");
    }

    public void startContainer(Long vmId) {
        String name = containerName(vmId);
        if (!containerExists(name)) {
            run("docker", "create", "--name", name, IMAGE, "sleep", "infinity");
        }
        runOptional("docker", "unpause", name);
        run("docker", "start", name);
    }

    public void pauseContainer(Long vmId) {
        run("docker", "pause", containerName(vmId));
    }

    public void stopContainer(Long vmId) {
        String name = containerName(vmId);
        runOptional("docker", "unpause", name);
        run("docker", "stop", name);
    }

    public void removeContainer(Long vmId) {
        runOptional("docker", "rm", "-f", containerName(vmId));
    }

    private boolean containerExists(String name) {
        try {
            Process p = new ProcessBuilder("docker", "inspect", name)
                    .redirectErrorStream(true)
                    .start();
            if (!p.waitFor(5, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                return false;
            }
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private void run(String... cmd) {
        executar(cmd, false);
    }

    private void runOptional(String... cmd) {
        executar(cmd, true);
    }

    private void executar(String[] cmd, boolean optional) {
        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            if (!p.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                p.destroyForcibly();
                if (!optional) log.warn("docker timeout: {}", String.join(" ", cmd));
                return;
            }
            int exit = p.exitValue();
            if (exit != 0 && !optional) {
                log.warn("docker exit {}: {}", exit, String.join(" ", cmd));
            }
        } catch (Exception e) {
            if (!optional) log.warn("docker erro [{}]: {}", String.join(" ", cmd), e.getMessage());
        }
    }
}
