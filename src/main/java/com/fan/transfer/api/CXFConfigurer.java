package com.fan.transfer.api;

import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;

public interface CXFConfigurer {
    Server getServer ();
    Bus getBus();
}
