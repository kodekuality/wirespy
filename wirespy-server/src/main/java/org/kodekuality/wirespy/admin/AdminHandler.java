package org.kodekuality.wirespy.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.kodekuality.wirespy.service.tcp.SpySource;
import org.kodekuality.wirespy.service.tcp.SpyTarget;
import org.kodekuality.wirespy.service.tcp.SpyTcpProxyService;
import org.kodekuality.wirespy.service.tcp.SpyTcpSession;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminHandler extends AbstractHandler {
    private final ObjectMapper objectMapper;
    private final SpyTcpProxyService spyTcpProxyService;

    public AdminHandler(ObjectMapper objectMapper, SpyTcpProxyService spyTcpProxyService) {
        this.objectMapper = objectMapper;
        this.spyTcpProxyService = spyTcpProxyService;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest httpRequest, HttpServletResponse response) throws IOException, ServletException {
        AddProxyRequest request = objectMapper.readValue(httpRequest.getInputStream(), AddProxyRequest.class);

        SpyTcpSession spyTcpSession = spyTcpProxyService.startSpySession(new SpySource(
                request.getSourceName(),
                request.getSourcePort()
        ), new SpyTarget(
                request.getTargetName(),
                request.getTargetHost(),
                request.getTargetPort()
        ));

        objectMapper.writeValue(response.getOutputStream(), new AddProxyResponse(
                spyTcpSession.getInStreamPort(),
                spyTcpSession.getOutStreamPort()
        ));

        response.setStatus(200);
        response.getOutputStream().close();
    }

}
