import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class order{
	private String OrderName;
	private List<Job> Jobs;
	private int operationCount;
	public int getOperationCount() {
		return operationCount;
	}
	public void setOperationCount(int operationCount) {
		this.operationCount = operationCount;
	}
	private int m;//m��Job
	public int getM() {
		return m;
	}
	public void setM(int m) {
		this.m = m;
	}
	public int getN() {
		return n;
	}
	public void setN(int n) {
		this.n = n;
	}
	private int n;//n̨����
	public String getOrderName() {
		return OrderName;
	}
	public void setOrderName(String orderName) {
		OrderName = orderName;
	}
	public List<Job> getJobs() {
		return Jobs;
	}
	public void setJobs(List<Job> jobs) {
		Jobs = jobs;
	}
	order(){
		
	}
	order(String orderName){
		this.OrderName = orderName;
	}
	public void setCapableHeavy() {
		List<machine> machines = new ArrayList();
		for(int i=0;i<this.getN();i++) {
			machine tempMachine = new machine();
			tempMachine.setMachineID(i+1);
			tempMachine.setMachineName("Machine"+(i+1));
			machines.add(tempMachine);
		}
		gant Gant = new gant().setMachines(machines);
		for(Job J:this.getJobs()) {
			for(stage S:J.getStages()) {
				for(capableMachine CMI:S.getCapableMachines()) {
					int machineID=CMI.getMachineId();
					int capable_stage_count = Gant.getMachines().get(machineID-1).getCapable_stage_count();
					Gant.getMachines().get(machineID-1).setCapable_stage_count(capable_stage_count+1);
				}
			}
		}
		//����stageCount��machines����
		for(int i =0 ; i<this.getN() ; i++) { 
            for(int j=0 ; j<this.getN()-1-i ; j++) {  
                if(machines.get(j).getCapable_stage_count()>machines.get(j+1).getCapable_stage_count()) {
                	Collections.swap(machines,j,j+1);
                }
            }    
        }		
		int machine_size = machines.size();
		for(int i=1;i<=machine_size;i++) {
			if(1.0*i/machine_size<=0.2) {
				machines.get(i-1).setMachineLoad(0);
			}else {
				machines.get(i-1).setMachineLoad(1);
			}
		}
		for(Job J:this.getJobs()) {
			for(stage S:J.getStages()) {
				for(capableMachine temp_machine:S.getCapableMachines()) {{
					int CMID = temp_machine.getMachineId();
					int heavy = machines.get(CMID-1).getMachineLoad();
					temp_machine.setHeavy(heavy);
				}
				}
			}
		}
	}
	public List<Job> read_file(String file_path){
		List<Job> table = new ArrayList();
		int m=-1,n=-1,operationCount=0;
		double l=-1;
		int job_count=-1;
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			 
			/* ����TXT�ļ� */
			String pathname = file_path; // ����·�������·�������ԣ������Ǿ���·����д���ļ�ʱ��ʾ���·��
			File filename = new File(pathname); // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			
			
			while (true) {
				job_count++;
				line = br.readLine(); // һ�ζ���һ������
				if(line==null || "".equals(line)) break;
				if(m==-1) {
					String[] tokens = line.trim().split("\\t");
					if(tokens.length==1)
						tokens = tokens[0].trim().split(" ");
					int t=0;
					while("".equals(tokens[t].trim()))
						t++;
					m=Integer.parseInt(tokens[t++]);
					while("".equals(tokens[t].trim()))
						t++;
					n=Integer.parseInt(tokens[t++]);
					while("".equals(tokens[t].trim()))
						t++;
					l=Double.parseDouble(tokens[t++].trim());
				}else {
					Job temp_Job = new Job("Job"+job_count);
					String[] tokens = line.trim().split(" ");
					int stage_counts = Integer.parseInt(tokens[0].trim());
					operationCount+=stage_counts;
					int ptr = 1;
					for(int i=0;i<stage_counts;i++) {
						stage temp_stage = new stage("stage"+(i+1));
						while("".equals(tokens[ptr].trim()))
							ptr++;
						int machine_count = Integer.parseInt(tokens[ptr++]);
						for(int j=0;j<machine_count;j++) {
							capableMachine temp_machine = new capableMachine();
							while("".equals(tokens[ptr].trim()))
								ptr++;
							temp_machine.setMachineId(Integer.parseInt(tokens[ptr]));
							while("".equals(tokens[ptr].trim()))
								ptr++;
							//System.out.println(temp_machine.getMachineId()+"  ptr:"+Integer.parseInt(tokens[ptr]));
							temp_machine.setMachineName("machine"+Integer.parseInt(tokens[ptr++]));
							while("".equals(tokens[ptr].trim()))
								ptr++;
							temp_machine.setCostTime(Integer.parseInt(tokens[ptr++].trim()));
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
		this.m=m;
		this.n=n;
		this.operationCount=operationCount;
		return table;
	}
	public String toString() {
		String output="";
		List<Job> list = this.getJobs();
		for(int i=0;i<list.size();i++) {
			Job temp = list.get(i);
			output=output+temp.toString()+"\n";
		}
		return output;
	}
	public static void main(String[] args) {
		order o = new order();
		o.setJobs(o.read_file("mt10cc.fjs"));
		System.out.print(o.toString());
	}
}
