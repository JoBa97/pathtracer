package me.joba.pathtracercluster.server;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import java.util.UUID;

/**
 *
 * @author balsfull
 */
public class ServerMain {
    
    public static UUID serverId;
    
    public static void main(String[] args) throws JSAPException {
        SimpleJSAP jsap = new SimpleJSAP(
                "PathTracer",
                "Realistic rendering",
                new Parameter[]{
                    new FlaggedOption("port", JSAP.INTEGER_PARSER, "1-65535", JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "port", "TCP Port"),
                    new FlaggedOption("height", JSAP.INTEGER_PARSER, "512", JSAP.REQUIRED, 'h', "height", "Image height"),
                    new FlaggedOption("width", JSAP.INTEGER_PARSER, "512", JSAP.REQUIRED, 'w', "width", "Image width"),
                    new FlaggedOption("scene", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "scene", "Scene file location"),
                    new FlaggedOption("servers", JSAP.INETADDRESS_PARSER, JSAP.NO_DEFAULT, JSAP.REQUIRED, JSAP.NO_SHORTFLAG, "servers", "Rendering servers"),
                    new FlaggedOption("continue", JSAP.STRING_PARSER, JSAP.NO_DEFAULT, JSAP.NOT_REQUIRED, JSAP.NO_SHORTFLAG, "continue", "Continue execution")
                }
        );
        JSAPResult config = jsap.parse(args);
        if (!config.success()) {
            System.err.println("                " + jsap.getUsage());
            System.exit(1);
        }
        serverId = UUID.randomUUID();
        
    }
}
