import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class order{
	private String _OrderName;
	private List<Job> _Jobs;
	private int _operationCount;
	private int _numberOfMachines;
	private int _numberOfJobs;
	private double _averageCapableNumberOfOperation;

	public double getAverageCapableNumberOfOperation() {
		return _averageCapableNumberOfOperation;
	}
	public void setAverageCapableNumberOfOperation(double averageCapableNumberOfOperation) {
		this._averageCapableNumberOfOperation = averageCapableNumberOfOperation;
	}
	public int getNumberOfMachines() {
		return _numberOfMachines;
	}
	public void setNumberOfMachines(int numberOfMachines) {
		this._numberOfMachines = numberOfMachines;
	}
	public int getNumberOfJobs() {
		return _numberOfJobs;
	}
	public void setNumberOfJobs(int numberOfJobs) {
		this._numberOfJobs = numberOfJobs;
	}
	public int getOperationCount() {
		return _operationCount;
	}
	public void setOperationCount(int operationCount) {
		this._operationCount = operationCount;
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
		return _OrderName;
	}
	public void setOrderName(String orderName) {
		_OrderName = orderName;
	}
	public List<Job> getJobs() {
		return _Jobs;
	}
	public void setJobs(List<Job> jobs) {
		_Jobs = jobs;
	}
	order(){
		
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
	/**
	 * The method read three number ,then update these number to the order.
	 * <p>
	 * this line is the first line of the instance.incude number of jobs,number of machines,the average capable number of operations.
	 * 
	 * @param anOrder the instance
	 * @param line numberOfJobs(int)"\\t"NumberOfJobs(int)"\\t"AverageCapableNumberOfOperation(double,may not exist)
	 */
	private void processFirstLine(order anOrder,String line) {
		String[] tokens = line.trim().split("\\t");
		int ptr=0;
		for(String token:tokens) {
			if("\\t".contentEquals(token)) {
				continue;
			}else {
				if(ptr==0) {
					final int numberOfJobs = Integer.parseInt(token);
					anOrder.setNumberOfJobs(numberOfJobs);
					anOrder.setM(numberOfJobs);
				}else if(ptr==1) {
					final int numberOfMachines = Integer.parseInt(token);
					anOrder.setNumberOfMachines(numberOfMachines);
					anOrder.setN(numberOfMachines);
				}else if(ptr==2) {
					final double averageCapableNumberOfOperation = Double.parseDouble(token);
					anOrder.setAverageCapableNumberOfOperation(averageCapableNumberOfOperation);
				}
				ptr++;
			}
		}
	}
	/**
	 * The method generate a Job by a content line. Then append the Job to allJobs.
	 * @param allJobs which include the all Job and Operation and capable machine in this instance.
	 * @param line 
	 */
	private void processAContentLine(List<Job> allJobs,String line) {
		String jobID = "Job"+(1+allJobs.size());
		Job temp_Job = new Job(jobID);
		String[] tokens = line.trim().split(" ");
		final int operationNumberOfThisJob = Integer.parseInt(tokens[0].trim());
		int ptr = 1;
		for(int i=0;i<operationNumberOfThisJob;i++) {
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
		allJobs.add(temp_Job);
	}
	/**
	 * Return BufferedReader br.
	 * @param file_path
	 * @return BufferedReader br
	 * @throws Exception
	 */
	private BufferedReader newABufferReader(String file_path) throws Exception{
		BufferedReader result = null;
		String pathname = file_path; // ����·�������·�������ԣ������Ǿ���·����д���ļ�ʱ��ʾ���·��
		File filename = new File(pathname); // Ҫ��ȡ����·����input��txt�ļ�
		InputStreamReader reader = new InputStreamReader(
				new FileInputStream(filename)); // ����һ������������reader
		result = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
		return result;
	}
	/**
	 * Return OperationCount of all Jobs.
	 * <p>
	 * Iterate allJobs then we can get the sum of operation of all jobs.
	 * @param allJobs
	 * @return sum of operation of all jobs
	 */
	private int caluOperationCount(List<Job> allJobs) {
		int result=0;
		for(Job Job:allJobs) {
			int operationNumberOfThisJob = Job.getStages().size();
			result+=operationNumberOfThisJob;
		}
		return result;
	}
	/**
	 * Returns an order object that can be used to generate scheduling instance.
	 * <p>
	 * This method always returns an order. 
	 *
	 * @param file_path is the url of instance
	 * @return the order format of instance 
	 */
	public order(String file_path){
		super();
		order result = this;
		List<Job> allJobs = new ArrayList();
		result.setJobs(allJobs);
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			/* ����TXT�ļ� */
			
			final BufferedReader br = newABufferReader(file_path);
			int ptrOfLine=0;
			while (true) {
				ptrOfLine++;
				String line = br.readLine(); // һ�ζ���һ������
				if(line==null || "".equals(line)) break;
				if(ptrOfLine==1) {
					processFirstLine(result,line);
				}else {
					processAContentLine(allJobs,line);
				}
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		final int operationCount = caluOperationCount(allJobs);
		result.setOperationCount(operationCount);
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
		String file_path = "Mk02.fjs";
		order o = new order(file_path);
		System.out.print(o.toString());
	}
}
