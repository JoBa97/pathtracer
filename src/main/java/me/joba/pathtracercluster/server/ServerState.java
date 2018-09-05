package me.joba.pathtracercluster.server;

import java.net.InetAddress;
import java.util.UUID;
import me.joba.pathtracercluster.pathtracer.Scene;
import me.joba.pathtracercluster.pathtracer.render.Plotter;

/**
 *
 * @author balsfull
 */
public class ServerState {
    
    public int port, height, width, autosave, writeImage;
    public InetAddress[] servers;
    public Scene scene;
    public UUID sceneId, serverId;
    public Plotter imageState;
}
