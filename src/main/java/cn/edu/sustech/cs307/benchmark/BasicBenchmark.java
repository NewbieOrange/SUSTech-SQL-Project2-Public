package cn.edu.sustech.cs307.benchmark;

import cn.edu.sustech.cs307.config.Config;
import cn.edu.sustech.cs307.factory.ServiceFactory;

public abstract class BasicBenchmark {
    protected ServiceFactory serviceFactory = Config.getServiceFactory();
}
