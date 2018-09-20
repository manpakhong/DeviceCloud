   <%@ page import="javax.management.*,javax.management.remote.*" %>
   <%@ page import="java.lang.management.*,java.util.List,java.io.PrintWriter,com.littlecloud.ac.health.*" %>
  <%
  		PrintWriter pw = response.getWriter();
	GCMonitor.installGCMonitoring();
	 pw.print(GCMonitor.popGcLog() + "<br/>");
	 Thread.sleep(10000);
	System.gc();
	Thread.sleep(10000);
	 pw.print(GCMonitor.popGcLog() + "<br/>");
	 pw.print("try new:" + GCMonitor2.getCurrentMemoryStatus() + "<br/>");
	 
	 
  
     MBeanServer mbeanServer = java.lang.management.ManagementFactory.getPlatformMBeanServer();
     pw.print("Got the MBeanServer.... "+mbeanServer + "<br/>");

     //ObjectName objectName=new ObjectName("test.startup:service=HelloWorld");

     // HelloWorldServiceMBean bean=new HelloWorldService();
     List<GarbageCollectorMXBean> bean =java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
     MemoryMXBean mem = java.lang.management.ManagementFactory.getMemoryMXBean();
     // mbeanServer.registerMBean(bean, objectName);

     
     pw.print("Hey:" + mem.getObjectName() + "," + mem.getHeapMemoryUsage() + ","+ mem.getNonHeapMemoryUsage() + "," + mem.getObjectPendingFinalizationCount());
     pw.print("<br/>");
     // System.out.println("MBean Registered with ObjectName:  "+objectName);
     for(GarbageCollectorMXBean ob: bean){
    	 for (String str: ob.getMemoryPoolNames()){
    	 	pw.print("memPool: " + str + "<br/>");
    	 }
    	 pw.print("---------<br/>");
    	 pw.print("name of memory manager:"+ob.getName() + "<br/>");
    	 pw.print("CollectionTime:"+ob.getCollectionTime() + "<br/>");
    	 pw.print("toString:" + ob.toString() + "<br/>");
    	 String[] str=ob.getMemoryPoolNames();
    	 for(int i=0;i<str.length;i++){
    		 pw.print(str[0].intern() + "<br/>");
    	 }
    }
  %>


