/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.traveltainment.jee.common.utility;

import com.traveltainment.jee.common.exception.AbstractException;
import com.traveltainment.jee.common.exception.AbstractSystemException;
import com.traveltainment.jee.common.exception.RoutingException;
import com.traveltainment.jee.common.messaging.message.Message;
import com.traveltainment.jee.common.utility.context.InvocationContextHelper;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import javax.interceptor.InvocationContext;
import static org.joox.JOOX.$;
import org.joox.Match;

/**
 *
 * @author bernat
 */
public class RoutingParser
{

    private static final String NS = "http://integra.traveltainment.de/routing";
    private static final String SERVICE_ELEMENT = "Service";
    private static final String ROUTING_ELEMENT = "Routing";

    public static List<String> getDestinations(final InvocationContext ctx) throws AbstractSystemException
    {
        final List<String> destinations = new ArrayList<>();
        InvocationContextHelper.checkInvocationContext(ctx);
        final Message m = InvocationContextHelper.getMessage(ctx);
        final String sender = ctx.getMethod().getDeclaringClass().getName();
        $(m.getRoutingInfo())
                .namespace("r", NS)
                .xpath("//r:" + SERVICE_ELEMENT + "[@Name='" + sender + "']")
                .each()
                .stream()
                .forEach((Match destination) ->
                        {
                            destinations.addAll(asList(destination.attr("Destinations").split("[\\s,;]+")));
                });
        if (destinations.isEmpty())
        {
            throw new RoutingException(m);
        }
        return destinations;
    }

    public static String getInVMScope(final InvocationContext ctx)
    {
        String inVMScope;

        if (ctx.getParameters() != null & ctx.getParameters().length > 0)
        {
            final Message m = (Message) ctx.getParameters()[0];
            inVMScope = $(m.getRoutingInfo())
                    .namespace("r", NS)
                    .xpath("//r:" + ROUTING_ELEMENT).attr("inVMScope");

        }
        else
        {
            inVMScope = "None";
        }

        return inVMScope;
    }

    /**
     *
     * @param ctx
     * @return
     */
    public static boolean isAsynchronous(final InvocationContext ctx) throws AbstractSystemException
    {
        return allAsync(ctx);

    }

    private static boolean allAsync(final InvocationContext ctx) throws AbstractSystemException
    {
        try
        {
            final List<String> destinations = RoutingParser.getDestinations(ctx);
            final boolean async = destinations.stream().filter((String d) ->
            {
                return d.contains("AsyncBeanLocal") || d.contains("AsyncBeanRemote");
            }).count() > 0;
            
            return async;
        }
        catch (AbstractException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new RoutingException(InvocationContextHelper.getMessage(ctx));
        }
    }

}
