package com.fct.common.logger;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jon on 2017/4/11.
 */
public class LogService {

    private static Logger logger;

    private LogService()
    {
        logger = Logger.getLogger("fctLog");
    }

    public static void info(String msg)
    {
        logger.log(Level.INFO,msg);
    }

    public static void info(String msg,Object param)
    {
        logger.log(Level.INFO,msg,param);
    }

    public static void info(String msg,Object[] params)
    {
        logger.log(Level.INFO,msg,params);
    }

    public static void info(String msg,Throwable thrown)
    {
        logger.log(Level.INFO,msg,thrown);
    }

    public static void debug(String msg)
    {
        logger.log(Level.ALL,msg);
    }

    public static void debug(String msg,Object param)
    {
        logger.log(Level.ALL,msg,param);
    }

    public static void debug(String msg,Object[] params)
    {
        logger.log(Level.ALL,msg,params);
    }

    public static void debug(String msg,Throwable thrown)
    {
        logger.log(Level.ALL,msg,thrown);
    }

    public static void warning(String msg)
    {
        logger.log(Level.WARNING,msg);
    }

    public static void warning(String msg,Object param)
    {
        logger.log(Level.WARNING,msg,param);
    }

    public static void warning(String msg,Object[] params)
    {
        logger.log(Level.WARNING,msg,params);
    }

    public static void warning(String msg,Throwable thrown)
    {
        logger.log(Level.WARNING,msg,thrown);
    }
}
