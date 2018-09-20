<%@page import="java.util.Properties"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.littlecloud.control.entity.viewobject.QuartzTriggers"%>
<%@page import="com.littlecloud.control.dao.branch.QzTriggersDAO"%>
<html>
<body>
<table>
<%
	HashMap<String,QuartzTriggers> triggersMap = null;
	try
	{
		triggersMap = QzTriggersDAO.getTriggersInfo();
	}
	catch(Exception e)
	{
		throw e;
	}
	boolean shortFlag = false;
	String shortFormat = request.getParameter("short");
	if(shortFormat!=null && shortFormat.equals("Y"))
		shortFlag = true;
	String url = "http://"+ request.getLocalAddr() +":8080/LittleCloud/";
	
	Set<String> keys = triggersMap.keySet();
%>
	<tr>
		<th>job group</th>
		<th>job status</th>
		<th>previous fire time</th>
		<th>next fire time</th>
		<th>pause job</th>
		<th>start job</th>
	</tr>
<%
	for( String key : keys )
	{
		String jg = key.replace('_', ' ');
%>
		<tr>
		<td nowrap width="30%"><% out.println(jg); %></td>
		<td nowrap width="10%"><% out.println(triggersMap.get(key).getStatus()); %></td>
		<td nowrap width="20%"><% out.println(new Date(triggersMap.get(key).getPrev_fire_time())); %></td>
		<td nowrap width="20%"><% out.println(new Date(triggersMap.get(key).getNext_fire_time())); %></td>
		<td nowrap width="10%"><a href="<%=url%>schedule_client.jsp?cmd=pause -job <%=key.toString().trim()%>">pause</a></td>
		<td nowrap width="10%"><a href="<%=url%>schedule_client.jsp?cmd=resume -job <%=key.toString().trim()%>">start</a></td>
		</tr>
<%
	}
	
%>
</table>
</body>
</html>