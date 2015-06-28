
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.apache.commons.io.FileUtils;

	class AosServer extends Thread
	{
	Aosclient c1=new Aosclient();
	String[] var;
	String message;
	ServerSocket listener=null;
    Socket socket,serverSocket;
    public static int ack=0;
    public static int displayHopCount=1;
	PrintWriter writerServer,tcpWriter;
	public static long y=0;
	public static ArrayList<String> searchInit=new ArrayList<String>();
	public static ArrayList<String> depart=new ArrayList<String>();
	public static int responseNo=0;
	public static int xoxo=0;
	//public static int counter=0;
	public static int a[]=new int[15];
	public static int noOfNo=0;
	public static int noOfNeighbors=0;
	
	FileReader fr = null;
    FileWriter fw = null;
    
	void serversendMessage(String clientname,int clientport,String msg)
	{
	try {
		serverSocket = new Socket(clientname,clientport);
		writerServer = new PrintWriter(serverSocket.getOutputStream(), true);
		writerServer.println(msg);
		System.out.println(msg+" Sent to client "+clientname+" at port "+clientport);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}
	public synchronized void run()
	{
 		try 
		{
		System.out.println("servername"+c1.name);
		int getCurrentPort=c1.getCurrentPort(c1.name);
		System.out.println("portnumber"+getCurrentPort);
    		listener = new ServerSocket(c1.getCurrentPort(c1.name));
		System.out.println(listener);
		
		} 
     
       catch (Exception e)
       	{
		// TODO Auto-generated 
		e.printStackTrace();
       	}
	   while (true) 
	   {   

		try 
		  {
			System.out.println("Enter the user input for the node to join or search or depart");
			socket = listener.accept();
			BufferedReader input =new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String message=input.readLine();
			System.out.println("onServerside " + message);
			var = message.split(" ");

			
          	       if(var[0].equals("JOIN"))
          	       {
          	    	   String joinNode=var[1];
          	    	   int joinPort=Integer.parseInt(var[2]);
          	    	   int m=c1.readP2PFile();
          	    	   String content=m+" "+joinNode+" "+joinPort+"\n";
          	    	   c1.updateFile(content);
          	    	   c1.updateNeighbourFile(joinNode,joinPort);
          	       }
          	     else if(var[0].equals("DEPART"))
        	       {
        	    	  String sCurrentLine;
        	    	  String var2[];
        	    	File fr=new File("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+c1.getCurrentNode(c1.name)+".txt");
             	   if(!fr.exists())
             	     {
             	     fr.createNewFile();
             	     }
          BufferedReader br = new BufferedReader(new FileReader(fr));
          File fw = new File("/home/004/a/ax/axj125730/Neighbours/NeighbourFileNew"+c1.getCurrentNode(c1.name)+".txt");
          BufferedWriter bw = new BufferedWriter(new FileWriter(fw,true));  
             	    		while ((sCurrentLine = br.readLine()) != null) 
             				{
             						var2 = sCurrentLine.split(" ");
             					    if(var2[1].equals(var[1]))
             					    {
             					    	continue;
             					    }
             					    else
             					    {
             					    	bw.write(sCurrentLine);
             					    	bw.write("\n");
             					    }
             				}
             	    		
                           
             	            br.close();  
             	            bw.close(); 
             	     boolean bool = fw.renameTo(fr);
             	     System.out.println("OK :"+bool);
             	     System.out.println("Neighbor successfully deleted!"); 
             	     
        	    	  if(c1.IamDeparting==0) 
        	    	  {
        	    		  String yes="YES";
        	    		  serversendMessage(var[1],c1.getCurrentPort(var[1]),yes);
        	    	  }
        	    	  else
        	    	  {
        	    		 String failure = "Failure";
        	    		 int n=c1.readNeighborFile(c1.name);
        	    			 for(int i=0;i<n;i++)
        	    			 {
        	    				if(!var[1].equals(c1.neighbor_Host[i]))
        	    				{
        	    					failure=failure+" "+c1.neighbor_Host[i]+" "+c1.name;
        	    				}
        	    			 }
        	    			 
        	   serversendMessage(var[1],c1.getCurrentPort(var[1]),failure); 
        	    		
        	    	  }
        	       }
          	   else if(var[0].equals("Failure"))
      	       {
      	    	System.out.println("Failure Received by"+c1.name);
    	    	depart.add(var[2]);
    	    	int rand=0;
    	    	ListIterator<String> departTest=depart.listIterator();
    	    	while(departTest.hasNext())
    	    	{
    	    		System.out.println("The departing nodes are "+departTest.next());
    	    	}
    	    	
    	    	int condition=1;int size=0;int mismatch=0;
    	    	Random r=new Random();
    	    	int noOfAttempt=0;
    	    	while(condition==1)
    	    	{
    	    		noOfAttempt++;
    	    		int n=c1.readNeighborFile(c1.name);
    	    		rand=r.nextInt(n);
    	    		System.out.println("The random node selected apart from depart nodes for the "+noOfAttempt+" attempt is "+c1.neighbor_Host[rand]);
    	    		ListIterator<String> dep=depart.listIterator();
    	    		while(dep.hasNext())
    	    		{	
    	    			size++;
    	    			if((dep.next()).equals(c1.neighbor_Host[rand]))
    	    			{
    	    				condition=1;
    	    			}
    	    			else
    	    			{
    	    			System.out.println("Random node not equal to "+size+" departing node");
    	    			mismatch++;
    	    			//condition=0;
    	    			}
    	    		}
    	   
    	    		if(mismatch==size)
    	    			condition=0;
    	    		
    	    	}
    	    	String reconnect="Reconnect";
    	    	int n=c1.readNeighborFile(c1.name);
    	    	for(int i=0;i<n;i++)
    	    	{
    	    		if(!c1.neighbor_Host[i].equals(var[1]))
    	    		{
    	    			reconnect=reconnect+" "+c1.neighbor_Host[i];
    	    		}
    	    	}
    	    	String reconnectFailure=reconnect+" "+message;
    	    	serversendMessage(c1.neighbor_Host[rand],c1.getCurrentPort(c1.neighbor_Host[rand]),reconnectFailure);
    	    	 //socket.close();
				 c1.deleteItselfFromP2P();
				 c1.deleteItsNeighborList();
				 
    	    	}

        	       
			else if(var[0].equals("SEARCH"))
			{
			int hopCount=Integer.parseInt(var[4]);
			hopCount=hopCount-1;
			 String responseHost=var[5];
			 String fileName=var[1];
			int portHost=c1.getCurrentPort(responseHost);
			if(hopCount==0)
			{
					searchInit.add(var[2]);
					String path=null;
					String delemeter=".";
					String result[]=c1.mySplit(c1.name,delemeter);
					File root = new File("/home/004/a/ax/axj125730/"+result[0]);
        			String fileName1 = "write.txt";
        			boolean recursive = true;
            		Collection files = FileUtils.listFiles(root, null, recursive);
            		for (Iterator iterator = files.iterator(); iterator.hasNext();) 
            		{
            		File file = (File) iterator.next();
            		if (file.getName().equals(fileName1))
                    path=file.getAbsolutePath();
            		}
	    			BufferedReader in = new BufferedReader(new FileReader(path));
	    			System.out.println("This is the path in which the file is present at Destination"+path);
	    			String s;
	    			int SearchEntry_file=0;
	    			int SearchEntry_Key=0;
	    			String path1=null;
	    		    int counter1=0;
	    		   
	    					while((s = in.readLine()) != null )
	        					{
	    						String[] var3 = s.split(" ");
	    							if(var3[0].equals(fileName))
	    							{
	    							SearchEntry_file=1;
	    							}
	    							else if(var3[1].equals(fileName))
	    							{
	    							SearchEntry_Key=1;	
	    							}
	    							else
	    							{
	    							System.out.println("File/Key is not present");
	    							}
	    						if(var3[0].equals(fileName) || var3[1].equals(fileName))
	    						{
	    						counter1++;
	    						System.out.println("Search Successful");
	    						
	    						if(SearchEntry_file==1)
	    						{
	    						path1="/home/004/a/ax/axj125730/"+result[0]+"/"+fileName;
	    						}
	    						else if(SearchEntry_Key==1)
	    						{
	    						path1="/home/004/a/ax/axj125730/"+result[0]+"/"+c1.getfileName(fileName); 	
	    						}
	    						else
	    						{System.out.println("FileName/Keyword(!=1)==in SEARCH");
	    						}	
	    						String msg="RESPONSE " +var3[0]+" "+c1.name+" "+var3[1]+" "+path1+" "+var[2];
	    						serversendMessage(responseHost,portHost,msg);
	    							ListIterator<String> dh=searchInit.listIterator();
	    							while(dh.hasNext())
	    							{
	    								String initT=dh.next();
	    								if(initT.equals(var[2]))
	    								{
	    									dh.remove();	
	    								}
	    							}
	    						}
	        					}
	    					if(counter1==0)
	    					{
	    						String msg="NO "+fileName+" "+c1.name+" "+var[2]+" "+var[6];
	    						serversendMessage(responseHost,portHost,msg);
	    						System.out.println("Search not Successful");
	    					}
			}
			if(hopCount!=0)
			{
			ListIterator<String> i=searchInit.listIterator();
			int Search=0;
			while(i.hasNext())
			    {
				String init=i.next();
				if(init.equals(var[2]))
					{
					int n=c1.readNeighborFile(c1.name);
						if((n-1)!=0)
						{
							String msg="SEARCH " +var[1]+" "+var[2]+" "+c1.getCurrentPort(var[2])+" "+hopCount+" "+c1.name+" "+var[5];
							c1.forward_SEARCH(msg,var[5]);
							Search=1;
						}
						else
						{
						String msg="NO "+fileName+" "+c1.name+" "+var[2]+" "+var[6];
    						serversendMessage(responseHost,portHost,msg);
    						System.out.println("Search not Successful");
							Search=1;
						}
					}
				}
					if(Search==0)
					{
						searchInit.add(var[2]);
						String path=null;
						String delemeter=".";
						String result[]=c1.mySplit(c1.name,delemeter);
						File root = new File("/home/004/a/ax/axj125730/"+result[0]);
	        			String fileName1 = "write.txt";
						
						
	        			boolean recursive = true;
	            		Collection files = FileUtils.listFiles(root, null, recursive);
	            		for (Iterator iterator = files.iterator(); iterator.hasNext();) 
	            		{
	            		File file = (File) iterator.next();
	            		if (file.getName().equals(fileName1))
	                    path=file.getAbsolutePath();
	            		}
		    			BufferedReader in = new BufferedReader(new FileReader(path));
		    			System.out.println("This is the path in which the file is present at Destination"+path);
		    			String s;
		    			int SearchEntry_file=0;
		    			int SearchEntry_Key=0;
		    			String path1=null;
		    		    int counter1=0;
		    					while((s = in.readLine()) != null )
		        					{
		    						String[] var3 = s.split(" ");
		    							if(var3[0].equals(fileName))
		    							{
		    							SearchEntry_file=1;
		    							}
		    							else if(var3[1].equals(fileName))
		    							{
		    							SearchEntry_Key=1;	
		    							}
		    							else
		    							{
		    							System.out.println("File/Key is not present");
		    							}
		    						if(var3[0].equals(fileName) || var3[1].equals(fileName))
		    						{
		    						counter1++;
		    						System.out.println("Search Successful");
		    						
		    						if(SearchEntry_file==1)
		    						{
		    						path1="/home/004/a/ax/axj125730/"+result[0]+"/"+fileName;
		    						}
		    						else if(SearchEntry_Key==1)
		    						{
		    						path1="/home/004/a/ax/axj125730/"+result[0]+"/"+c1.getfileName(fileName); 	
		    						}
		    						else
		    						{System.out.println("FileName/Keyword(!=1)==in SEARCH");
		    						}	
		    						String msg="RESPONSE " +var3[0]+" "+c1.name+" "+var3[1]+" "+path1+" "+var[2];
		    						serversendMessage(responseHost,portHost,msg);
		    						}
		        					}
		    					if(counter1==0)
		    					{
		    						System.out.println("Search not Successful");
		    						c1.readNeighborFile(c1.name);
		    						String msg="SEARCH " +var[1]+" "+var[2]+" "+c1.getCurrentPort(var[2])+" "+hopCount+" "+c1.name+" "+var[5];
		    						c1.forward_SEARCH(msg,var[5]);
		    					}
							}
					
					
						}
			}
			else if(var[0].equals("RESPONSE"))
			{
				AosResThread r=new AosResThread(var[1],var[2],var[3],var[4],var[5]);
				r.start();
			}

			else if(var[0].equals("SENDFILE"))
			{
			String msg="FILERCVD"+" "+var[1]+" "+var[2]+" "+c1.name+" "+var[4];
			serversendMessage(var[3],c1.getCurrentPort(var[3]),msg);
			}
			else if(var[0].equals("FILERCVD"))
			{String delemeter=".";
			String result[]=c1.mySplit(c1.name,delemeter);
			File root = new File("/home/004/a/ax/axj125730/"+result[0]);
			String fileName1 = "write.txt";
				try
				{
			        fr = new FileReader(var[2]);
			        System.out.println("The path  ==="+var[2]);
			        File file=new File("/home/004/a/ax/axj125730/"+result[0]+"/"+var[1]);
			    	if(!file.exists())
			    	{
			    	
			    		file.createNewFile();
			    	}
			    	
			        fw = new FileWriter("/home/004/a/ax/axj125730/"+result[0]+"/"+var[1]);
			        int c = fr.read();
			        while(c!=-1) {
			        	System.out.println("Writing the file");
			            fw.write(c);
			            c = fr.read();
			        			}
				}
				catch(IOException e) {
			        e.printStackTrace();
			    	} 
				finally 
			    	{
			        try {
						fr.close();
						fw.close();
						} 
			        catch (IOException e) 
						{
						// TODO Auto-generated catch block
						e.printStackTrace();
						}
			        }
			FileWriter fw2 = new FileWriter("/home/004/a/ax/axj125730/"+result[0]+"/"+"write.txt",true);
			String newCopiedFile=var[1]+" "+var[4];
			fw2.append("\n");
			fw2.append(newCopiedFile);
			fw2.flush();
			fw2.close();	
				}
          	 
			 else if(var[0].equals("YES"))
    	       {
    	    	   ack++;
    	    	  // System.out.println("The no of new neighbours "+c1.noOfNeighbours);
    	    	   //System.out.println("The no of old neighbours "+c1.oldNeighbours);
    	    	   if(ack==c1.oldNeighbours)
    	    	   {
    	    		 ack=0;
    	    		 c1.send_Reconnect();
					
					 c1.deleteItselfFromP2P(); 
					 c1.deleteItsNeighborList();
					// socket.close();
    	    	   }
    	    	   
    	       }
			else if(var[0].equals("Reconnect"))
   	       {
   	    	   System.out.println("Received Reconnect by "+c1.name);
   	    	   int i=0;int y;String s = null;int found=0;String[] s1=null;
   	    	 String reconnectToNeighbours[]=new String[15];
   	    	   /* Getting the neighbors from the Recoonect Msg */
   	    	StringTokenizer st = new StringTokenizer(message);
   	    	   //Iterate through all the neighbor list contents and add if needed
   	    	while (st.hasMoreElements()) {
   	    		String moderator =(String) st.nextElement();
   	    		System.out.println("The list of neighbors which it needs to add: "+moderator);
   	    		if(!moderator.equals("Failure") && !moderator.equals("Reconnect"))
 				{
 				reconnectToNeighbours[i]=moderator;
   				i++;
   				}
   			}
   	 
   	    	 
   	   		for(int i1=0;i1<i;i1++)
   	   		{
   	   			int condition=1;
   	   			found=0;
   	   			BufferedReader br=null;BufferedWriter bw=null;
   	   			String[] s2=null;
   	   			br = new BufferedReader(new FileReader("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+c1.getCurrentNode(c1.name)+".txt"));
   	   			bw = new BufferedWriter(new FileWriter("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+c1.getCurrentNode(c1.name)+".txt",true)); 
   	   			 while(condition==1)
   	   		      	{
   	   				 s=br.readLine();
   	   				 if(s!=null)
   	   				 {	s2=s.split(" ");
   	   		      	  if(reconnectToNeighbours[i1].equals(s2[1])){found=1;}
   	   		      	 // else{System.out.println("Neighbour not found");}
   	   				 }
   	   				 else
   	   				 {
   	   					 condition=0;
   	   				 }
   	   		      	}
   	   System.out.println("The value of found"+found);
   	   		      if(found==0)
   	   		      {	  
   	   		      bw.append(c1.getCurrentNode(reconnectToNeighbours[i1])+" "+reconnectToNeighbours[i1]+" "+c1.getCurrentPort(reconnectToNeighbours[i1]));
   	   		      bw.append("\n");
   	   		      bw.flush();
   	   		      bw.close();
   	   		      /*===After adding as its neighbour.,it needs to establish connection
   	   		       * with the neighbour.
   	   		       */
   	   		      String connection="CONNECTION "+c1.name;
   	   		      serversendMessage(reconnectToNeighbours[i1],c1.getCurrentPort(reconnectToNeighbours[i1]),connection);
   	   		      }
   	   			 }
   	   		}
          	       
			 else if(var[0].equals("CONNECTION"))
    	       {
    	    	   /*=====Add the unconnected node to its neighbour list======*/
    	   BufferedWriter bwriter = new BufferedWriter(new FileWriter("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+c1.getCurrentNode(c1.name)+".txt",true));
    	   bwriter.append(c1.getCurrentNode(var[1])+" "+var[1]+" "+c1.getCurrentPort(var[1]));
    	   bwriter.append("\n");
    	   bwriter.flush();
		   bwriter.close();
    	       }
				
			else if(var[0].equals("NO"))
			{
				int forward=0;
				ListIterator iter6 =c1.initSearchNamevc.listIterator();
				ListIterator iter8 = c1.fileKeyvc.listIterator();
				ListIterator iter61 =c1.senderSearchNamevc.listIterator();
				ListIterator iter80 = c1.receiverSearchNamevc.listIterator();
				while (iter6.hasNext() && iter8.hasNext() && iter61.hasNext() && iter80.hasNext()) 
				{
			    String init=(String) iter6.next();
			    String sender=(String) iter61.next();
			    String filekey=(String) iter8.next();
			    String receiver=(String) iter80.next();
			    		if(var[3].equals(init))
			    			{
			    			if(var[3].equals(c1.name))
			    				forward=1;
			    			}
				}
				int n=c1.readNeighborFile(c1.name);
				System.out.println("Printing the forward value NO "+forward);
						if(forward==1)	
			    		{
			    			noOfNo++;
			    				if( noOfNo==n)
			    				{
			    					c1.hopCount=c1.hopCount*2;
			    					if(c1.hopCount<=16)
			    						{	
			    						c1.removeSearchRequest_Initiator(var[3]);
			    						/*Initiate a new Search Request */
			    						displayHopCount++;
			    						c1.readNeighborFile(var[1],c1.hopCount,var[3],null);
			    						noOfNo=0;
			    						}
			    					else
			    						{   
			    						c1.removeSearchRequest_Initiator(var[3]);
			    						System.out.println("File not present in p2p");
			    						c1.userInterface();
			    						}
			    				}
			    		}
			    		else 
			    		{
			    			a[c1.getCurrentNode(var[3])]++;
			    			int n1=c1.readNeighborFile(c1.name);
			    			 if(a[c1.getCurrentNode(var[3])]==(n1-1))
			    			 {
			    				c1.removeSearchRequest_Initiator(var[3]); 
			    				String msg="NO "+var[1]+" "+c1.name+" "+var[3];
			   /* Forwarding 'NO' to the previous node from where
			    * it received the Search Request and not to init
			    */
			    				serversendMessage(var[4],c1.getCurrentPort(var[4]),msg);
			    				a[c1.getCurrentNode(var[3])]=0;
			    			 }
			    		}
			    						
			    	}}
		catch (Exception e)
       	{
		// TODO Auto-generated catch block
		e.printStackTrace();
       	}
		}}	
			    
	}
	
	class AosResThread extends Thread
	{
		
		Aosclient c=new Aosclient();
		AosServer s=new AosServer();
		String var1,var2,var3,var4,var5;
		public static int x=0;
		String replyNumber;
		public static int dontEnter=0;
		public static List<String> Key=Collections.synchronizedList(new ArrayList<String>());
		public static List<String> file=Collections.synchronizedList(new ArrayList<String>());
		public static List<String> comp=Collections.synchronizedList(new ArrayList<String>());
		public static List<String> path=Collections.synchronizedList(new ArrayList<String>());
		ListIterator iter9;
		ListIterator iter4;
		ListIterator iter;
		ListIterator iter8;
		AosResThread(String var1,String var2,String var3,String var4,String var5)
		{
			this.var1=var1;this.var2=var2;this.var3=var3;this.var4=var4;this.var5=var5;
		}
		
	 public synchronized void run()	
	{
		
	int dontEnter=0;
	int forward=0;
	String Searchreceiver = null;
	
	iter9 =c.initSearchNamevc.listIterator();
	iter4 =c.senderSearchNamevc.listIterator();
	iter = c.fileKeyvc.listIterator();
	iter8 = c.receiverSearchNamevc.listIterator();
	while (iter.hasNext() && iter4.hasNext() && iter9.hasNext() && iter8.hasNext()) 
	{
    String fileKey=(String) iter.next();
   
    String initname=(String)iter9.next();
    System.out.println("initial sender "+initname);
    System.out.println("var5 "+var5);
    String sender=(String) iter4.next();
    Searchreceiver=(String) iter8.next();
    	if(var5.equals(initname))
    	{
    		//System.out.println("If initname=var5");
    		if(var5.equals(c.name))
    		{
    			forward=1;
    			//System.out.println("If var5=name..,then making forward=1");
    			}
    	}
	}
	if(forward==1)
	{
		while(((System.currentTimeMillis())-(c.x))<(1000))
		{
	    		ListIterator iter11=file.listIterator();
	  		ListIterator iter21=Key.listIterator();
	  		ListIterator iter31=comp.listIterator();
	  		ListIterator iter41=path.listIterator();
			ListIterator iter51=path.listIterator();
	  		AosServer.responseNo++;
	  		 
			    	  
	  	if((iter11.hasNext()==false) && (iter21.hasNext()==false) && (iter31.hasNext()==false) && (iter41.hasNext()==false))
	  	{
	  		synchronized(file)
	  		{
	  		
	  		file.add(var1);
		    	  Key.add(var3);
		    	  comp.add(var2);
		    	  path.add(var4);
		    	 System.out.println("The filename search "+file.get(s.responseNo-1));
		    	 System.out.println("The filekey search "+Key.get(s.responseNo-1));
		    	 System.out.println("The computername "+comp.get(s.responseNo-1));
		    	 System.out.println("The pathname "+path.get(s.responseNo-1));
		    	 dontEnter=1;
	  	}}
	  	else{
	  	if(dontEnter==0)
	  	{
	  		synchronized(file)
	  		{	
	  	while (iter11.hasNext() && iter21.hasNext() && iter31.hasNext() && iter41.hasNext()) 
	  	{
	  		if(((iter11.next()).equals(var1))&&((iter21.next()).equals(var3))&&((iter31.next()).equals(var2)) && ((iter41.next()).equals(var4)))
	  		{
	  			System.out.println("it has the response dont add");
	  		}
	  			else
	  			{
	    		file.add(var1);
		    	  Key.add(var3);
		    	  comp.add(var2); 
		    	  path.add(var4);
		    	 
	  			}

	  	}
	  		}
	  	}
	  	}
		} 	 
				ListIterator iter1=file.listIterator();
				ListIterator iter2=Key.listIterator();
				ListIterator iter3=comp.listIterator();
				
				int counter1=0;
				while(iter1.hasNext() && iter2.hasNext() && iter3.hasNext())
				{
				System.out.println(counter1+" "+(String)iter2.next()+" "+(String)iter1.next()+" "+(String)iter3.next());
				counter1=counter1+1;
				}
				
				c.removeSearchRequest_Initiator(var5);
				System.out.println("Search is successful");
				System.out.println("hopcount of search: "+s.displayHopCount);
				System.out.println("Elapsed time for successful replies: "+((System.currentTimeMillis())-(c.x)));
				System.out.println("Select one among the above tuples and enter the index of it");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				int dt=0;
				while(dt==0)
				{  
					
						try {
							replyNumber=br.readLine();	
							if(replyNumber!=null)
							{
								System.out.println("No of replies"+replyNumber);
								x=Integer.parseInt(replyNumber);
								dt=1;
							}
						}	
						catch (Exception e) 
						{
							e.printStackTrace();
						}
				}
				String msg="SENDFILE"+" "+var1+" "+path.get(x)+" "+c.name+" "+var3;
				s.serversendMessage(comp.get(x),c.getCurrentPort(comp.get(x)),msg);
				clearResponseVectors();	
				
				c.userInterface();
				}
	else
	{
  	  System.out.println("Forwarding the responses");
  	  String msg="RESPONSE " +var1+" "+var2+" "+var3+" "+var4+" "+var5;
  	  s.serversendMessage(Searchreceiver,c.getCurrentPort(Searchreceiver),msg);
  	  	ListIterator<String> dh=s.searchInit.listIterator();
  	  	while(dh.hasNext())
  	  	{
  	  		String initT=dh.next();
  	  		if(initT.equals(var5))
  	  		{
  			  dh.remove();	
  	  		}
  	  	}
	}	
	}
	
	void clearResponseVectors()
	{
		ListIterator iter11=file.listIterator();
		ListIterator iter21=Key.listIterator();
		ListIterator iter31=comp.listIterator();
		while (iter11.hasNext() && iter21.hasNext() && iter31.hasNext()) 
			{
			String d=(String) iter11.next();
			String du=(String) iter21.next();
			String dum=(String) iter31.next();
			iter11.remove();
			iter21.remove();
			iter31.remove();
			}
	}
}
	class Aosclient
	{
	
	public static List<String> fileKeyvc=Collections.synchronizedList(new ArrayList<String>());
	public static List<String> initSearchNamevc=Collections.synchronizedList(new ArrayList<String>());
	public static List<String> senderSearchNamevc=Collections.synchronizedList(new ArrayList<String>());
	public static List<String> receiverSearchNamevc=Collections.synchronizedList(new ArrayList<String>());
	public static String[] neighbor_Host=new String[15];
	public static int[] neighbor_Port=new int[15];
	public static String[] hostname=new String[15];
	public static int[] portnumber=new int[15];
	public static int[] nodenumber=new int[15];
	int[] nodeIndex=new int[15];
	String[] nodeName=new String[15];
	int[] nodePort=new int[15];
	public static String name;
	public static long x=0;
	Socket clientSocket;
	public static int IamDeparting=0;
	public static int oldNeighbours=0;
	public PrintWriter writer;
	public FileWriter fw,fw1;
	public static int ind_neighbor=0;
	public static int noOfNeighbours=0;
	public static int hopCount=1;
	int totalNodes=15;
	public static Logger logger;
	public int y=0;
	synchronized void removeSearchRequest_Initiator(String initiator)
	{
	ListIterator iter91 =initSearchNamevc.listIterator();
	ListIterator iter41 =senderSearchNamevc.listIterator();
	ListIterator iter10 = fileKeyvc.listIterator();
	ListIterator iter81 = receiverSearchNamevc.listIterator();
	synchronized(initSearchNamevc)
	{
	while (iter91.hasNext() && iter41.hasNext() && iter10.hasNext() && iter81.hasNext()) 
		{
    String fileKey=(String) iter10.next();
    String initname=(String)iter91.next();
    String sender=(String) iter41.next();
    String Searchreceiver=(String) iter81.next();
	 		if(initiator.equals(initname))
	 			{
	 			iter91.remove();
	 			iter41.remove();
	 			iter10.remove();
	 			iter81.remove();
	 			}	
		}
	}
	}
	int random(int no_nodes_P2P)
	{
	Random rand = new Random();
	int R = rand.nextInt(no_nodes_P2P);
	return R;
	}

	public synchronized void updateNeighbourFile(String joinNode, int joinPort) {
		// TODO Auto-generated method stub
		neighbourFileCreation();
		try {
			String content=getCurrentNode(joinNode)+" "+joinNode+" "+joinPort+"\n"; 
			fw1.write(content);
			fw1.flush();
			fw1.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public synchronized void updateFile(String content) {
		// TODO Auto-generated method stub
		P2PFileCreation();
		try {
			fw.write(content);
			fw.flush();
			fw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	void getName()
	{
	try 	
	{
	name = InetAddress.getLocalHost().getHostName();
	}
	catch (Exception e) 
	{
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	}
	public static String[] mySplit(String text,String delemeter){
	    java.util.List<String> parts = new java.util.ArrayList<String>();

	    text+=delemeter;

	    for (int i = text.indexOf(delemeter), j=0; i != -1;) {
	        parts.add(text.substring(j,i));
	        j=i+delemeter.length();
	        i = text.indexOf(delemeter,j);
	    }

	    return parts.toArray(new String[0]);
	}
	void join(int no_nodes_P2P)
	{
		
	if(no_nodes_P2P==0)
		{
		System.out.println("Entering into if of join method");
		String content= no_nodes_P2P+" "+name+" "+getCurrentPort(name)+"\n";
		System.out.println(content);
		try {
		System.out.println("writing content into the file");
			fw.write(content);
			fw.flush();
			fw.close();
			} catch (IOException e) 
			{
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}
	else
		{
		System.out.println("no. of nodes more than 1");
		int rn=random(no_nodes_P2P);
		System.out.println("rand num"+rn);
		System.out.println("random node is"+nodeName[rn]);
		System.out.println("The nodePort[rand] is"+nodePort[rn]);
		String msg="JOIN " +name+" "+getCurrentPort(name);
		clientSender(nodeName[rn],nodePort[rn],msg);
		String content=getCurrentNode(nodeName[rn])+" "+nodeName[rn]+" "+nodePort[rn]+"\n"; 
		try {
				fw1.write(content);
				fw1.flush();
				fw1.close();
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}}}
	public void neighbourFileCreation() 
	{

		File file1=new File("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+getCurrentNode(name)+".txt");
		if(!file1.exists())
		{
			try {
				file1.createNewFile();
				
				}
			catch (IOException e) 
				{
			// TODO Auto-generated catch block
			e.printStackTrace();
				}
		}
		try {
			fw1=new FileWriter(file1.getAbsoluteFile(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void readNeighborFile(String filename,int hopCount,String name1,String receiver)
	{
		int count=0;
		int i=0;
		BufferedReader br1;
		String sCurrentLine=null;
		String[] var2;
		String msg = null;
		try
		{
br1 = new BufferedReader(new FileReader("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+getCurrentNode(name)+".txt"));			
			
			while ((sCurrentLine = br1.readLine()) != null) 
			{
					var2 = sCurrentLine.split(" ");
					//System.out.println("var2[1]: "+var2[1]);
				    neighbor_Host[i]=var2[1];
					neighbor_Port[i]=Integer.parseInt(var2[2]);
					msg="SEARCH " +filename+" "+name1+" "+getCurrentPort(name1)+" "+hopCount+" "+name+" "+receiver;
					sending_SEARCH(neighbor_Host[i],neighbor_Port[i],msg);
					i++;
			}
			
			addToQueue(msg);	
			
		}
		catch(Exception e)
		{
		e.printStackTrace();
		}
		noOfNeighbours=i;
	}
void readOldNeighbourFile(String myNeighborfile)
	{
		int i=0;
		BufferedReader br1;
		String sCurrentLine=null;
		String[] var2;
		String msg = null;
		try
		{
br1 = new BufferedReader(new FileReader("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+getCurrentNode(name)+".txt"));			
			
			while ((sCurrentLine = br1.readLine()) != null) 
			{
					var2 = sCurrentLine.split(" ");
					//System.out.println("var2[1]: "+var2[1]);
				    neighbor_Host[i]=var2[1];
					neighbor_Port[i]=Integer.parseInt(var2[2]);
					i++;
			}	
		}
		catch(Exception e)
		{
		e.printStackTrace();
		}
		noOfNeighbours=i;
	oldNeighbours=noOfNeighbours;	

	}
	int readNeighborFile(String myNeighborfile)
	{
		int i=0;
		BufferedReader br1;
		String sCurrentLine=null;
		String[] var2;
		String msg = null;
		try
		{
br1 = new BufferedReader(new FileReader("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+getCurrentNode(name)+".txt"));			
			
			while ((sCurrentLine = br1.readLine()) != null) 
			{
					var2 = sCurrentLine.split(" ");
					//System.out.println("var2[1]: "+var2[1]);
				    neighbor_Host[i]=var2[1];
					neighbor_Port[i]=Integer.parseInt(var2[2]);
					i++;
			}	
		}
		catch(Exception e)
		{
		e.printStackTrace();
		}
		noOfNeighbours=i;
		
		return i;
	}
	
/*=====Delete its neighbors list=====================================*/
	void deleteItsNeighborList()
	{
		
File f=new File("/home/004/a/ax/axj125730/Neighbours/NeighbourFile"+getCurrentNode(name)+".txt");			
boolean tf=f.delete();
System.out.println("NeighborFile of the node is deleted "+tf);	
	}
/*=============================sending_SEARCH===========================================*/
	void sending_SEARCH(String neighbor_Host,int neighbor_Port,String msg)
	{
		x=System.currentTimeMillis();
		clientSender(neighbor_Host,neighbor_Port,msg);
		
	}
/*===========================Forward SEARCH==========================================*/
	void forward_SEARCH(String msg,String searchSender)
	{
		int n=readNeighborFile(name);
		for(int i=0;i<n;i++)	
		{
			if(!neighbor_Host[i].equals(searchSender))
			{
			clientSender(neighbor_Host[i],neighbor_Port[i],msg);
			}
		}
		addToQueue(msg);
	}
	
	
	
	/*=========================CREATING A NEW SOCKET ON CLIENT SIDE TO SEND JOIN,SEARCH,DEPART
	 * ===============================================================================MESSAGES
	 */
	void clientSender(String neighbor_Host,int neighbor_Port,String msg)
	{
		try 	
		{			
			clientSocket = new Socket(neighbor_Host,neighbor_Port);
			writer = new PrintWriter(clientSocket.getOutputStream(), true);
			writer.println(msg);
			System.out.println(msg+" Sent to Host "+neighbor_Host+" at port "+neighbor_Port);
		}
	 catch (IOException e) 
	 {
		// TODO Auto-generated catch block
		e.printStackTrace();
	 }
	}
	
	void send_DEPART()
	 {
		 String msg="DEPART "+name;
		 for(int i = 0;i<noOfNeighbours;i++)
		 {
		  clientSender(neighbor_Host[i],neighbor_Port[i],msg); 
		
		 }
	 }
	
	void send_Reconnect()
	{
		//GENERATING A RANDOM NUMBER
		Random r=new Random();
		int n=readNeighborFile(name);
		int rand=r.nextInt(n);
		String reconnect = "Reconnect";
		// LIST OF NEIGHBOURS TO THAT RANDOM NODE
			 for(int i=0;i<noOfNeighbours;i++)
			 {
				if(!neighbor_Host[rand].equals(neighbor_Host[i]))
				{
					reconnect=reconnect+" "+neighbor_Host[i];
				}
			 }
			 //SENDING RECONNECT MESSAGE TO A RANDOM NODE
	  		clientSender(neighbor_Host[rand],neighbor_Port[rand],reconnect); 
	}
	
	/*=========ADDING REQUESTS TO ITS VECTOR QUEUE===================================*/
	void addToQueue(String msg)
	{
	String[] var2;
	var2 = msg.split(" ");
	fileKeyvc.add(var2[1]);
	initSearchNamevc.add(var2[2]);
	senderSearchNamevc.add(var2[5]);
	receiverSearchNamevc.add(var2[6]);

	}
	
	/*=======Delete the departing node from P2P list================*/
	void deleteItselfFromP2P() throws IOException
	{
		 
		 File fr=new File("/home/004/a/ax/axj125730/config.txt");
    	   if(!fr.exists())
    	     {
    	     fr.createNewFile();
    	     }
		 File fw=new File("/home/004/a/ax/axj125730/ListNew.txt");
		 BufferedWriter out= null;BufferedReader in = null;
		try {
			 in = new BufferedReader(new FileReader(fr));
			out = new BufferedWriter(new FileWriter(fw,true));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
    	String s1;
    	while((s1 = in.readLine()) != null )
        {
		String[] var = s1.split(" ");
		if(var[1].equals(name))
			continue;
		else
		{
			try {
				out.write(s1);
				out.write("\n");
				out.flush();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
			
        }
    	out.close();
    	boolean bool = (fw).renameTo(fr);
 	    System.out.println("The boolean :"+bool);
 	    System.out.println("Departing node deleted from the config file"); 
	}


/*==========================Shared P2P file Creation===================================*/

	void P2PFileCreation()
	{
		File file=new File("/home/004/a/ax/axj125730/config.txt");
		if(!file.exists())
		{
		try {
			file.createNewFile();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}
		try {
		fw=new FileWriter(file.getAbsoluteFile(),true);
		} catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

/*======== Reading  P2P file to get the no of nodes in the Pear to Pear System=====================*/

	int readP2PFile()
	{
	int i=0;
	  try
	  	{
	    	BufferedReader in = new BufferedReader(new FileReader("config.txt"));
	    	String s;
		while((s = in.readLine()) != null )
	        {
	        String[] var = s.split(" ");
	        nodeName[i]=var[1];
	        nodePort[i]=Integer.parseInt(var[2]);
	        nodeIndex[i]=Integer.parseInt(var[0]);
	        i++;
		 y=i;
	        }
		
		}
		catch(Exception e)
      		{
	    	e.printStackTrace();
	    	}
	  System.out.println("number of nodes in P2P file"+y);
	  return y;
	}


/*======== Reading file to get Hosts and PortNumbers=====================*/

	void nodefile()
	{
	int i=0;
	  try
	  	{
	    	BufferedReader in = new BufferedReader(new FileReader("nodefile"));
	    	String s;
	    	while((s = in.readLine()) != null )
	        {
	        String[] var = s.split(" ");
	        hostname[i]=var[1];
	        //System.out.println(hostname[i]);
	        portnumber[i]=Integer.parseInt(var[2]);
	        //System.out.println(portnumber[i]);
	        nodenumber[i]=Integer.parseInt(var[0]);
	        //System.out.println(nodenumber[i]);
	        i++;
	        }
		}
		catch(Exception e)
      		{
	    	e.printStackTrace();
	    	}
	}

/*============= To obtain the current port number==========================*/
	public int getCurrentPort(String name)
	{
	//System.out.println(name);
	for(int i=0;i<totalNodes;i++)
	  {
	   //System.out.println("Printing the hostname[i] in getCurrentPort() "+hostname[i]);
	   if(name.equals(hostname[i]))	
	     {	

		 return portnumber[i];
	     }
	  }
    	return 0;
    	}
	
	
	public int getCurrentNode(String name)
	{
	
	for(int i=0;i<totalNodes;i++)
	  {
	   
	   if(name.equals(hostname[i]))	
	     {	

		 return nodenumber[i];
	     }
	  }
    	return 0;
    	}
	
	public String getfileName(String key)
	{
		try {
			String delemeter=".";
			String result[]=mySplit(name,delemeter);
			BufferedReader in = new BufferedReader(new FileReader("/home/004/a/ax/axj125730/"+result[0]+"/"+"write.txt"));
			String s;
			int i=0;
			String s1[]=null;
			while((s = in.readLine()) != null )
	        {
	            s1=s.split(" ");
	       		if(s1[1].equals(key))
	       		{
	    	   return s1[0];
	       		}
	       		i++;
	        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
	}


	void readInput()
	{
	 System.out.print("Do you want to search for a file(Yes/No): ");
	 BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
 	 String decision = null;
	 String FileName;
 	 try {
         decision = br.readLine();
		if(decision.equals("Yes"))
			{
		System.out.print("Enter file name to search: ");
		BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
		FileName= br1.readLine();
		sendSEARCH(FileName);
			}
		else
			{
		System.out.println("I dont want to search");
		userInterface();
			}
      	   } 
	 catch (IOException ioe) 
	    {
        System.out.println("IO error trying to read your name!");
        System.exit(1);
        }
	 }


	void sendSEARCH(String filename)
	{
	readNeighborFile(filename,hopCount,name,null);
	}
	
	void userInterface()
		{
		BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
		String input;
		int permission=0;
		while(permission==0)
		{
		try {
			input = br.readLine();
			
					if(input.equals("J"))
					{
						
						
						int no_nodes=readP2PFile();
						neighbourFileCreation();
						join(no_nodes);
					}
					else if(input.equals("S"))
					{
						readInput();
						permission=1;
					}
					else if(input.equals("D"))
					{IamDeparting=1;
					 
					  readOldNeighbourFile(name);
					  int n=readNeighborFile(name);
					  System.out.println("No of neighbours to the node which want to depart: "+n);
					  send_DEPART();
					}
				
			} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
									}
		}}}

	class AOSPJ2
	{
	public static void main(String args[]) throws InterruptedException
		{
	Aosclient c=new Aosclient();
	c.getName();
    c.nodefile();
	c.P2PFileCreation();
	AosServer s=new AosServer();
	s.start();
	c.userInterface();
		}
	}

