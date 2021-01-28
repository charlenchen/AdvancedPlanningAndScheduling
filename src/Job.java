import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Job {
	 
	public static String file_path= "Mk01.txt";
	private String jobName;
	public String getJobName() {
		return jobName;
	}
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	public List<stage> getStages() {
		return stages;
	}
	public Job setStages(List stages) {
		this.stages = stages;
		return this;
	}
	private List<stage> stages;
	public Job add(stage s) {
		this.stages.add(s);
		return this;
	}
	Job(){
		this.stages = new ArrayList();
	}
	Job(String jobName){
		this.stages = new ArrayList<stage>();
		this.jobName = jobName;
	}
	public String toString() {
		String output = this.jobName+"\n";
		int len_stage = this.getStages().size();
		for(int i=0;i<len_stage;i++) {
			stage s = this.getStages().get(i);
			int len_machine = s.getCapableMachines().size();
			output = output+s.getStageName()+"\t";
			for(int j=0;j<len_machine;j++) {
				capableMachine machine = s.getCapableMachines().get(j);
				output = output+"  "+machine.getMachineName()+" "+machine.getCostTime();
				
			}
			output=output+"\n";
			
		}
		
		return output;
	}
	public static void test() {
		List<Job> table = new ArrayList();
		int m=-1,n=-1,l=-1;
		int job_count=-1;
		try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
			 
			/* 读入TXT文件 */
			String pathname = file_path; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
			File filename = new File(pathname); // 要读取以上路径的input。txt文件
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); // 建立一个输入流对象reader
			BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
			String line = "";
			
			
			while (true) {
				job_count++;
				line = br.readLine(); // 一次读入一行数据
				if(line==null || "".equals(line)) break;
				if(m==-1) {
					String[] tokens = line.trim().split(" ");			
					m=Integer.parseInt(tokens[0]);
					n=Integer.parseInt(tokens[1]);
					l=Integer.parseInt(tokens[2].trim());
				}else {
					Job temp_Job = new Job("Job"+job_count);
					String[] tokens = line.trim().split(" ");
					int stage_counts = Integer.parseInt(tokens[0].trim());
					int ptr = 1;
					for(int i=0;i<stage_counts;i++) {
						stage temp_stage = new stage("stage"+(i+1));
						int machine_count = Integer.parseInt(tokens[ptr++]);
						for(int j=0;j<machine_count;j++) {
							capableMachine temp_machine = new capableMachine();
							temp_machine.setMachineId(Integer.parseInt(tokens[ptr]));
							temp_machine.setMachineName("machine"+Integer.parseInt(tokens[ptr++]));
							temp_machine.setCostTime(Integer.parseInt(tokens[ptr++]));
							temp_stage.add(temp_machine);
						}
						temp_Job.add(temp_stage);
					}
					table.add(temp_Job);
				}
				
			}
			
 
		} catch (Exception e) {
			e.printStackTrace();

				
		}
		System.out.println("m:"+m+" n:"+n+" l:"+l);
		int len = table.size();
		for(int i=0;i<len;i++) {
			System.out.print(table.get(i).toString());
		}
	}
	public static void main(String[] args) {
		test();
	}
}
class capableMachine{
//class capableMachine implements Comparable<capableMachine>{
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public int getCostTime() {
		return costTime;
	}
	public void setCostTime(int costTime) {
		this.costTime = costTime;
	}
	private int heavy;
	
	public int getHeavy() {
		return heavy;
	}
	public void setHeavy(int heavy) {
		this.heavy = heavy;
	}
	private String machineName;
	private int costTime;
	private int machineId;
	public int getMachineId() {
		return machineId;
	}
	public void setMachineId(int machineId) {
		this.machineId = machineId;
	}
	capableMachine() {
		
	}
	capableMachine(String name,int time) {
		this.machineName=name;
		this.costTime=time;
	}
//	@Override
//	public int compareTo(capableMachine o) {
//		// TODO Auto-generated method stub
//		if(this.getCostTime()<o.getCostTime())
//			return -1;
//		else if(this.getCostTime()==o.getCostTime())
//			return 0;
//		else
//			return 1;
//	}
}
 class stage{
	private String stageName;
	private List<capableMachine> capableMachines;
	public stage add(capableMachine m) {
		this.capableMachines.add(m);
		return this;
		
	}
	stage(){
		this.capableMachines = new ArrayList();
	}
	stage(String stageName){
		this.stageName = stageName;
		this.capableMachines = new ArrayList();
	}
	public List<capableMachine> getCapableMachines() {
		return this.capableMachines;
	}
	public String getStageName() {
		return this.stageName;
	}
}