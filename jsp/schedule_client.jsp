<%@page import="java.net.Socket"%>
<%@page import="java.io.OutputStreamWriter"%>
<%@page import="java.io.BufferedWriter"%>

<html>
<body>
<% 
try 
{
	String command = request.getParameter("cmd");
	System.out.println("cmd="+command);
	Socket sock = new Socket(request.getLocalAddr(), 28300);
	OutputStreamWriter osw = new OutputStreamWriter(sock.getOutputStream());
	BufferedWriter bw = new BufferedWriter(osw);
	bw.append(command);
	bw.flush();
	bw.close();
	osw.close();
	sock.close();
	out.println(command+" is completed!");
} 
catch (Exception e) 
{
	// TODO Auto-generated catch block
	e.printStackTrace();
}
finally
{
	response.setStatus(response.SC_MOVED_TEMPORARILY);
	response.setHeader("Location","http://"+ request.getLocalAddr() +":8080/LittleCloud/schedule_manager.jsp");
}
%>
</body>
</html>
