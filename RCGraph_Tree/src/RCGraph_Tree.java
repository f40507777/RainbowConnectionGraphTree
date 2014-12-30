import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.tree.DefaultMutableTreeNode;

/*
 * 2014/12/30 何建毅 f4050777@gmail.com
 * 
 * 路徑所有排列組合
 * 利用樹狀圖來達到不重複數字效果
 * 每個路徑用arraylist方式儲存，並存於node上
 * 剩餘路線利用arraylist方式儲存
 * 再利用HashMap將剩餘路線與路徑做成表(路徑,剩餘路線)
 */


public class RCGraph_Tree {
	//input graph filename
	static String FileName="RCGraph50_0.5_2.txt";
	//graph size (auto)
	static int size;
	//input graph
	static String[][] graph;
	//rc path record
	static int[][] recordGraph;
	//visit node count
	static int visitcount;
	//start time
	static long Starttime;
	//rule-2 jump
	static int rule2=0;
	//Root node
	static DefaultMutableTreeNode Root =new DefaultMutableTreeNode("Root");
	//record node Edge
	static HashMap<DefaultMutableTreeNode, ArrayList<Integer>> NodeHashMap =new HashMap<DefaultMutableTreeNode, ArrayList<Integer>>();
	//start node
	static int startNode=-1;
	
	public static void main(String[] args) throws IOException{
		//start time
		Starttime = System.currentTimeMillis();
		//Setting
		Setting();
		//rule-3
		if(Rule3()){
			//Create Tree
			Create_Tree(Root,0);
			System.out.println("visit="+visitcount);
			printOutput(Check(),"");
			
		}
		System.out.println("Time：" + (float)(System.currentTimeMillis()-Starttime)/1000 + "秒");
	}
	
	public static void Create_Tree(DefaultMutableTreeNode node,int level){
		visitcount++;
		
		//是否回到root

		for(int i=NodeHashMap.get(node).size()-1;i>=0;i--){
			ArrayList<Integer>NodePath=new ArrayList<Integer>();
			if(node.getUserObject() != "Root"){
				NodePath=(ArrayList<Integer>) ((ArrayList<Integer>) node.getUserObject()).clone();
			}else{
				if(rule2==size-1){
					//jump歸零
					rule2=0;
				}else if(startNode!=-1){
					printOutput(false,"Rule4("+startNode+")");
					return;
				}
				startNode++;
				System.out.println("startNode="+startNode);
				NodeHashMap.clear();
				NodeHashMap.put(Root, AllpathArray());

			}
			
			NodePath.add(NodeHashMap.get(node).get(i));
			DefaultMutableTreeNode Child =new DefaultMutableTreeNode(NodePath);
			level++;
			System.out.println("parent="+startNode+"	level="+level+"	Node="+node+"	Child="+Child);
			
			//未使用方法(正常修剪不通的道路)
			if(level<size+1 && connectBool(level,NodePath)){
				//計算是否有彩虹路徑
				recordgrpah(NodePath);
				//複製剩下路徑給Childlist
				ArrayList<Integer>Childlist =(ArrayList<Integer>) NodeHashMap.get(node).clone();
				//刪除當下的node以防止Child重複
				Childlist.remove(i);
				NodeHashMap.put(Child, Childlist);
				node.add(Child);
				Create_Tree(Child,level);
			}
			
			//方法一(相同顏色則修剪)
			/*if(level<size+1 && recordgrpah(NodePath)){
				//複製剩下路徑給Childlist
				ArrayList<Integer>Childlist =(ArrayList<Integer>) NodeHashMap.get(node).clone();
				//刪除當下的node以防止Child重複
				Childlist.remove(i);
				NodeHashMap.put(Child, Childlist);
				node.add(Child);
				Create_Tree(Child,level);
			}*/
			level--;
			
			//方法2
			/*if(rule2==size-1){
				//退回到root
				if(level!=0){
					break;
				}
			}*/
			
		}
	}
public static Boolean recordgrpah(ArrayList<Integer> Node){
		
		if(Node.size()>1){
			Set<String> colors = new HashSet<>();
			for(int i=0;i<Node.size()-1;i++){
				if(graph[Node.get(i)][Node.get(i+1)].equals("X")){
					return false;
				}else{
					colors.add(graph[Node.get(i)][Node.get(i+1)]);
				}
			}
			//路徑統計完.是否顏色數=路徑數-1
			if(colors.size()==Node.size()-1){
				if((recordGraph[Node.get(0)][Node.get(Node.size()-1)])!=1){
					recordGraph[Node.get(0)][Node.get(Node.size()-1)]=1;
					//recordGraph[Node.get(Node.size()-1)][Node.get(0)]=1;//對稱
					rule2++;
				}
				return true;
			}else{
				return false;
			}
		}else{
			//root點，可以生成child
			return true;
		}

		
	}
	public static void Setting() throws IOException{
		//read file to "graph" String array
		ReadFile();
		NodeHashMap.put(Root, AllpathArray());
	}
	
	public static Boolean Rule3(){
		//this node compare other node have same color if edge count only one
		ArrayList<String>somecolor = new ArrayList<String>();
		ArrayList<Integer>node = new ArrayList<Integer>();
		for(int i=0;i<graph.length;i++){
			//計算不相通"X"數
			int Xcount=0;
			String tmpcolor="";
			for(int j=0;j<graph[i].length;j++){
				if(graph[i][j].equals("X")){
					Xcount++;
				}else if(i!=j){
					tmpcolor=graph[i][j];
				}
			}
			if(Xcount>=size-2){
				
				for(int q=0;q<somecolor.size();q++){
					if(somecolor.get(q).equals(tmpcolor)){
						printOutput(false,"Rule3("+node.get(q)+","+i+")");
						return false;
					}
				}
				node.add(i);
				somecolor.add(tmpcolor);
			}
		}
		return true;
	}
	
	public static void ReadFile() throws IOException{
		FileReader fr = new FileReader(FileName);
		BufferedReader br = new BufferedReader(fr);
		String str;
		int count=0;
		while ((str = br.readLine())!=null) {
			if(count==0){
				//delete "[" and "]" and splite "," count graph size 
				size=((str.substring(1,str.length()-1)).split(", ")).length;
				graph=new String [size][size];
				recordGraph=new int [size][size];
			}
			str=str.substring(1,str.length()-1);//去頭尾[]
			graph[count] = str.split(", ");
			count++;
			System.out.println(str);
		}
		fr.close();
	}
	public static ArrayList<Integer> AllpathArray(){
		ArrayList<Integer>Indexlist = new ArrayList<Integer>();
		for(int k=size-1;k>-1;k--){
			Indexlist.add(k);
		}
		return Indexlist;
	}
	
	public static void printOutput(Boolean TF,String message){
		System.out.println();
		if(TF){
			System.out.println("RainBow　Connection Graph");
		}else{
			System.out.println("Not RainBow　Connection Graph");
		}
		System.out.println(message);
	}
	public static Boolean Check(){
		int countisRanbowGraph=0;
		for(int i=0;i<recordGraph.length;i++){
			System.out.println(Arrays.toString(recordGraph[i]));
			for(int j=0;j<recordGraph[i].length;j++){
				if(recordGraph[i][j]==1){
					countisRanbowGraph++;
				}
			}
		}
		if(countisRanbowGraph==size*(size-1)){
			return true;
		}else{
			return false;
		}
	
	}
	public static Boolean connectBool(int level,ArrayList<Integer> NodePath){
		if(level>1 && graph[NodePath.get(level-2)][NodePath.get(level-1)].equals("X")){
			return false;
		}
			return true;
	}
}
