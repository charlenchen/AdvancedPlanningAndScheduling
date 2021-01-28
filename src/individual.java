import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class individual implements Comparable<individual>{
	private chromosome chromosome;
	private static final int INF = 65535;
	public chromosome getChromosome() {
		return chromosome;
	}
	public boolean equals(Object o) {
		if(this==o) return true;
		if(o==null || getClass()!=o.getClass()) return false;
		individual I = (individual) o;
		int [] IMS = I.getChromosome().getMachineSelection();
		int [] IOS = I.getChromosome().getStageSequence();
		int [] thisMS = this.getChromosome().getMachineSelection();
		int [] thisOS = this.getChromosome().getStageSequence();
		boolean flag = true;
		int len=IMS.length;
		for(int i=0;i<len;i++) {
			if(thisMS[i]!=IMS[i]) {
				flag=false;
				break;
			}
			if(thisOS[i]!=IOS[i]) {
				flag=false;
				break;
			}
		}
		return flag;
	}
	public int hashCode() {
		chromosome C = this.getChromosome();
		String hash = "";
		for(int i:C.getMachineSelection()) {
			hash+=i;
		}
		for(int i:C.getStageSequence()) {
			hash+=i;
		}
		return hash.hashCode();
		
	}
	public individual setChromosome(chromosome chromosome) {
		this.chromosome = chromosome;
		return this;
	}
	public String toString() {
		return this.getTotalCost()+"";
	}
	public decode getDecode() {
		return decode;
	}
	public void setDecode(decode decode) {
		this.decode = decode;
	}
	public gant getGant() {
		return gant;
	}
	public void setGant(gant gant) {
		this.gant = gant;
	}
	public sequence getSequence() {
		return sequence;
	}
	public void setSequence(sequence sequence) {
		this.sequence = sequence;
	}
	private int totalCost;
	public int getTotalCost() {
		return totalCost;
	}
	public void setTotalCost(int totalCost) {
		this.totalCost = totalCost;
	}
	private decode decode;
	private gant gant;
	private sequence sequence;
	public void constructJm_T(GA G,List<ArrayList<Integer>> Jm,List<ArrayList<Integer>> T,int [] machine_selection){
		order o =  G.getOrder();
		int M = G.getOrder().getM();
		int N = G.getOrder().getN();
		int ptrToMS=0;
		
		for(int m=0;m<M;m++) {
			Job temp_job=o.getJobs().get(m);
			ArrayList<Integer> temp_stage_ID = new ArrayList();
			Jm.add(temp_stage_ID);
			ArrayList<Integer> temp_stage_cost = new ArrayList();
			T.add(temp_stage_cost);
			for(int var1=0;var1<temp_job.getStages().size();var1++) {
				stage temp_stage = temp_job.getStages().get(var1);
				for(int var2=1;var2<=temp_stage.getCapableMachines().size();var2++) {
					if(machine_selection[ptrToMS]==var2) {
						int machineID=temp_stage.getCapableMachines().get(var2-1).getMachineId();
						int cost=temp_stage.getCapableMachines().get(var2-1).getCostTime();
						temp_stage_ID.add(machineID);
						temp_stage_cost.add(cost);
						ptrToMS++;
						break;
					}
				}
			}
		}
	}
	public void constructGantAndSequence(individual i,int M,int N) {
		gant g = new gant();
		i.setGant(g);
		List<machine> machines = new ArrayList();
		for(int n=0;n<N;n++) {
			machine temp_machine = new machine();
			temp_machine.setMachineID(n+1);
			temp_machine.setMachineName("machine"+(n+1));
			ArrayList<node> temp_node_list = new ArrayList<node>();
			temp_node_list.add(new node().setMachine_id(n+1).setFinishTime(0).setStartTime(0));
			temp_machine.setNodes(temp_node_list);
			machines.add(temp_machine);
		}
		g.setMachines(machines);
		int [] stage_sequence = i.getChromosome().getStageSequence();
			//��stage_sequence�����ҽ��н���
		int [] appears = new int[M];//n��Job��ÿһ������Ҫ���м�����ȷ�ϵ�ǰ������һ������
		for(int var1=0;var1<M;var1++) appears[var1]=0;//��ʼ����Ϊ0
		sequence sequences = new sequence();
		ArrayList<List<node>> list = new ArrayList();
		for(int n=0;n<M;n++) {
			ArrayList<node> temp_list = new ArrayList();
			temp_list.add(new node().setMachine_id(n+1).setFinishTime(0).setStartTime(0));
			list.add(temp_list);
		}
		sequences.setJob_process(list);
		i.setSequence(sequences);
	}
	public void constructNodes(individual I,int M,int[] MS,int[] OS,List<ArrayList<Integer>> Jm,List<ArrayList<Integer>> T) {
		//�������е�OS����ÿһ��OS��Ӧ�Ļ���������Ӧ�ڵ�
		int len=OS.length;
		int [] appears = new int[M];for(int a:appears) a=0;
		for(int var1=0;var1<len;var1++) {
			int Job_ID = OS[var1];//����var1�ϵ���ҵ
			int Operation = ++appears[Job_ID-1];//����var1����ҵ���е��˵Ĺ���
			int Machine_ID = Jm.get(Job_ID-1).get(Operation-1);//�ù������ҵ�Ļ�����
			int Cost = T.get(Job_ID-1).get(Operation-1);//�ù���Ļ���ʱ��
			node temp_node = new node();//�������ͼ�ڵ�
			temp_node.setJob_id(Job_ID).setStage_id(Operation).setMachine_id(Machine_ID);
			//����ǰ�����ϵ�����Ϊ�գ������
			node pre_Oper_node = null;
			if(Operation!=1) {//��ǰ������ǰ�ù���
				pre_Oper_node = I.getSequence().getJob_process().get(Job_ID-1).get(Operation-1);
			}
			machine CurMachine = I.getGant().getMachines().get(Machine_ID-1);
			int VAR2 = CurMachine.getNodes().size();
			for(int var2=0;var2<VAR2;var2++) {
				node curNode = CurMachine.getNodes().get(var2);
				node nextNode = null;
				if(var2==CurMachine.getNodes().size()-1) {
					//�޺�����ҵ�����·���
				}else {
					nextNode = CurMachine.getNodes().get(var2+1);
				}
				if(nextNode==null) {
					//�޺�����ҵ������ѭ�����һ��
					//�Ƿ����ǰ�ù���
					if(pre_Oper_node==null) {
						//��ǰ�ù��򣬿�ֱ���ڸû����ϼӹ�
						int startTime = curNode.getFinishTime();
						int finishTime = Cost+startTime;
						temp_node.setStartTime(startTime);
						temp_node.setFinishTime(finishTime);
						I.getSequence().getJob_process().get(Job_ID-1).add(temp_node);
						I.getGant().getMachines().get(Machine_ID-1).getNodes().add(temp_node);
					}else {
						//��ǰ�ù��򣬿�����Ҫ�ȴ�
						int startTime = pre_Oper_node.getFinishTime()>curNode.getFinishTime()?pre_Oper_node.getFinishTime():curNode.getFinishTime();
						int finishTime = Cost+startTime;
						temp_node.setStartTime(startTime);
						temp_node.setFinishTime(finishTime);
						I.getSequence().getJob_process().get(Job_ID-1).add(temp_node);
						I.getGant().getMachines().get(Machine_ID-1).getNodes().add(temp_node);
					}
				}else {
					//���ں�����ҵ�����ܿ��Բ嵽������ҵ��϶��
					int startTime;
					if(pre_Oper_node==null) {
						startTime=curNode.getFinishTime();
					}else {
						startTime= pre_Oper_node.getFinishTime()>curNode.getFinishTime()?pre_Oper_node.getFinishTime():curNode.getFinishTime();
					}
					int finishTime = startTime + Cost;
					if(finishTime>nextNode.getStartTime()) {
						//�岻��ȥ�����·���������ѭ��
					}else {
						//�ܲ�����ҵ��϶,�����ֹͣѭ��
						temp_node.setStartTime(startTime);
						temp_node.setFinishTime(finishTime);
						I.getSequence().getJob_process().get(Job_ID-1).add(temp_node);
						I.getGant().getMachines().get(Machine_ID-1).getNodes().add(temp_node);
						break;
					}
					
				}
			}
		}
		
		
	}
	public void decode2(GA G) {
		order O = G.getOrder();
		chromosome C = this.getChromosome();
		int [] MS = C.getMachineSelection();
		int [] OS = C.getStageSequence();
		individual I = this;
		List<ArrayList<Integer>> Jm = new ArrayList();
		List<ArrayList<Integer>> T  = new ArrayList();
		int M = O.getM();
		int N = O.getN();
		constructJm_T(G,Jm,T,MS);
		constructGantAndSequence(I,M,N);
		constructNodes(I,M,MS,OS,Jm,T);
		//���øø�����ܺ�ʱ
		int maxCost = 0;
		for(machine m:this.getGant().getMachines()) {
			node n = m.getNodes().get(m.getNodes().size()-1);
			if(n.getFinishTime()>maxCost)
				maxCost=n.getFinishTime();
		}
		this.setTotalCost(maxCost);		
	}
	public void decode(GA G) {
		order o = G.getOrder();
//		order o = new order();
//		o.setJobs(o.read_file("MK01.txt"));
//		o.setJobs(o.read_file("test.txt"));
//		System.out
		individual i = this;
		//Ԥ�������Ⱦɫ��
		
//		chromosome c = new chromosome();
//		c.setMachineSelection(new int[] {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1});
//		c.setStageSequence(new int[]    {1,1,1,1,1,1,2,2,2,2,2,3,3,3,3,3,4,4,4,4,4,5,5,5,5,5,5,6,6,6,6,6,6,7,7,7,7,7,8,8,8,8,8,9,9,9,9,9,9,10,10,10,10,10,10});;
//		c.setMachineSelection(new int[] {4,1,2,2,4});
//		c.setStageSequence(new int[] {2,2,1,1,2});	
//		i.setChromosome(c);
		chromosome c = this.getChromosome();
		//��Ԥ���Ⱦɫ����н���
		decode d = new decode();
		i.setDecode(d);
		int [] machine_selection = i.getChromosome().getMachineSelection();
		int ptrToMS=0;
		List<ArrayList<Integer>> Jm = new ArrayList();
		List<ArrayList<Integer>> T = new ArrayList();
		int M=o.getM();
		int N=o.getN();
		for(int m=0;m<M;m++) {
			Job temp_job=o.getJobs().get(m);
			ArrayList<Integer> temp_stage_ID = new ArrayList();
			Jm.add(temp_stage_ID);
			ArrayList<Integer> temp_stage_cost = new ArrayList();
			T.add(temp_stage_cost);
			for(int var1=0;var1<temp_job.getStages().size();var1++) {
				stage temp_stage = temp_job.getStages().get(var1);
				for(int var2=1;var2<=temp_stage.getCapableMachines().size();var2++) {
					if(machine_selection[ptrToMS]==var2) {
						int machineID=temp_stage.getCapableMachines().get(var2-1).getMachineId();
						int cost=temp_stage.getCapableMachines().get(var2-1).getCostTime();
						temp_stage_ID.add(machineID);
						temp_stage_cost.add(cost);
						ptrToMS++;
						break;
					}
				}
			}
		}
		//������Jm��T����Ҫ��������-ʱ��ͼ������ͼ���빤��-ʱ��ͼ(sequence)
		gant g = new gant();
		i.setGant(g);
		List<machine> machines = new ArrayList();
		for(int n=0;n<N;n++) {
			machine temp_machine = new machine();
			temp_machine.setMachineID(n+1);
			temp_machine.setMachineName("machine"+(n+1));
			ArrayList<node> temp_node_list = new ArrayList<node>();
			temp_node_list.add(new node().setMachine_id(n+1).setFinishTime(0).setStartTime(0));
			temp_machine.setNodes(temp_node_list);
			machines.add(temp_machine);
		}
		g.setMachines(machines);
		int [] stage_sequence = i.getChromosome().getStageSequence();
			//��stage_sequence�����ҽ��н���
		int [] appears = new int[M];//n��Job��ÿһ������Ҫ���м�����ȷ�ϵ�ǰ������һ������
		for(int var1=0;var1<M;var1++) appears[var1]=0;//��ʼ����Ϊ0
		sequence sequences = new sequence();
		ArrayList<List<node>> list = new ArrayList();
		for(int n=0;n<M;n++) {
			ArrayList<node> temp_list = new ArrayList();
			temp_list.add(new node().setMachine_id(n+1).setFinishTime(0).setStartTime(0));
			list.add(temp_list);
		}
		sequences.setJob_process(list);
		i.setSequence(sequences);
		for(int var1=0;var1<stage_sequence.length;var1++) {
			int j = stage_sequence[var1];//��j������
			int h=++appears[j-1];//��j�������ĵ�h������
			int machineId = Jm.get(j-1).get(h-1);
			int cost = T.get(j-1).get(h-1);
			node temp_node = new node();
			temp_node.setJob_id(j).setStage_id(h).setMachine_id(machineId);
			if(g.getMachines().get(machineId-1).getNodes().size()==0+1) {
				//����ù�������������ǵĵ�һ���������0��ʼ������Ӹù������һ������Ľ����ӹ�ʱ�俪ʼ���ɡ�
				if(h==1) {
					//�Ӹû�����0ʱ�̿�ʼ�ӹ�
					temp_node.setStartTime(0).setFinishTime(cost);
					g.getMachines().get(machineId-1).getNodes().add(temp_node);
					i.getSequence().getJob_process().get(j-1).add(temp_node);
				}else {
					node prenode = i.getSequence().getJob_process().get(j-1).get(h-1);
					int preNodeFinisheTime = prenode.getFinishTime();
					temp_node.setStartTime(preNodeFinisheTime).setFinishTime(preNodeFinisheTime+cost);
					g.getMachines().get(machineId-1).getNodes().add(temp_node);
					i.getSequence().getJob_process().get(j-1).add(temp_node);
				}
			}else {
				//Ѱ���ܹ����ɵ�ʱ����
				node sequencePreNode = i.getSequence().getJob_process().get(j-1).get(h-1);
				int finishTime = sequencePreNode.getFinishTime();
				int VAR2=g.getMachines().get(machineId-1).getNodes().size();
				for(int var2=0;var2<VAR2;var2++) {
					node curNode = g.getMachines().get(machineId-1).getNodes().get(var2);
					//���������һ���ڵ㣬����ڼ��ʱ�䡣
					node nextNode = null;
					if(var2+1<g.getMachines().get(machineId-1).getNodes().size()) {
						nextNode = g.getMachines().get(machineId-1).getNodes().get(var2+1);
					}else {
						//��������һ���ڵ㣬��ǰ�������һ���ڵ㡣�����ʼʱ��ȡ�ϻ�����ǰ���������ʱ����ù����ϸ���������ʱ��Ĵ�ֵ
						int ta = curNode.getFinishTime()>sequencePreNode.getFinishTime()?curNode.getFinishTime():sequencePreNode.getFinishTime();
						temp_node.setStartTime(ta).setFinishTime(ta+cost);
						g.getMachines().get(machineId-1).getNodes().add(temp_node);
						i.getSequence().getJob_process().get(j-1).add(temp_node);
					}
					if(nextNode!=null) {
						//Ѱ����һ�������ӹ��Ĺ������ʱ������һ������ļӹ����ʱ�䣬ȡ��
						//taΪ�ϸ��ڵ�����ʱ��������ϸ���������ʱ��Ĵ�ֵ
						int ta = curNode.getFinishTime()>sequencePreNode.getFinishTime()?curNode.getFinishTime():sequencePreNode.getFinishTime();
						if(ta+cost<=nextNode.getStartTime()) {
							//���Բ��룬��������ѭ��
							temp_node.setStartTime(ta).setFinishTime(ta+cost);
							g.getMachines().get(machineId-1).getNodes().add(var2+1,temp_node);
							i.getSequence().getJob_process().get(j-1).add(temp_node);
							break;
						}
						//���ܲ��룬����Ѱ�Ҽ��
					}
				}	
			}
		}
		//��øø�����ܺ�ʱ
		int maxCost = 0;
		for(machine m:this.getGant().getMachines()) {
			node n = m.getNodes().get(m.getNodes().size()-1);
			if(n.getFinishTime()>maxCost)
				maxCost=n.getFinishTime();
		}
		this.setTotalCost(maxCost);
//		System.out.println("finished  cost:"+maxCost);	
	}
	public static void main(String[] args) {
//		new individual().decode();
	}
	
	@Override
	public int compareTo(individual o) {
		// TODO Auto-generated method stub
		if(this.getTotalCost()<o.getTotalCost())
			return -1;
		else if(this.getTotalCost()==o.getTotalCost())
			return 0;
		return 1;
	}
	public void toSamplePart1_Location(GA G) {
		for(List<node> J: this.getSequence().getJob_process()) {
			int J_stage_size = J.size();
			int count=0;
			for(node N:J) {
				if(count==1) {
					N.getSample().setLocation(1);
				}else if(count==J_stage_size-1) {
					N.getSample().setLocation(4);
				}else {
					if(1.0*count/J_stage_size<=0.5) {
						//ǰһһ�빤��
						N.getSample().setLocation(2);
					}else {
						//��һ�빤��
						N.getSample().setLocation(3);
					}
				}
				count++;
			}
		}
	}
	public void toSamplePart2_Cost(GA G,List<node> all_nodes,int COUNT_O,int COUNT_N) {
		//ð������
		for(int i =0 ; i<COUNT_N ; i++) { 
            for(int j=0 ; j<COUNT_N-1-i ; j++) {  
                if(all_nodes.get(j).getCost()>all_nodes.get(j+1).getCost()) {
                	Collections.swap(all_nodes,j,j+1);
            }
            }    
        }
		//�ӹ�ʱ���Ϊ���࣬���г�
		double [] COST_Stand = new double[] {0.33,0.66,1.0};
		for(int i=1;i<=COUNT_N;i++) {
			if(1.0*i/COUNT_O<=COST_Stand[0]) {
				all_nodes.get(i).getSample().setCost(1);
			}else if(1.0*i/COUNT_N<=COST_Stand[1]) {
				all_nodes.get(i-1).getSample().setCost(2);
			}else {
				all_nodes.get(i-1).getSample().setCost(3);
			}
		}
	}
	public void toSamplePart3_RemainTime(GA G,List<node> all_nodes,int COUNT_O,int COUNT_N) {
		//����ʣ��ʱ��
		for(List<node> J:this.getSequence().getJob_process()) {
			int J_stage_size = J.size();
			int total_cost = 0;
			for(node N:J) {
				total_cost+=N.getCost();
			}
			int remain_time=total_cost;
			for(node N:J) {
				remain_time -= N.getCost();
				N.setRemain_time(remain_time);
			}
		}
			//ð������
		for(int i =0 ; i<COUNT_N ; i++) { 
            for(int j=0 ; j<COUNT_N-1-i ; j++) {  
                if(all_nodes.get(j).getRemain_time()>all_nodes.get(j+1).getRemain_time()) {
                	Collections.swap(all_nodes,j,j+1);
            }
            }    
        }
		//ʣ��ʱ���Ϊ���࣬���г�
		double [] Remain_Stand = new double[] {0.33,0.66,1.0};
		
		for(int i=0;i<COUNT_N;i++) {
			if(1.0*i/COUNT_N<=Remain_Stand[0]) {
				all_nodes.get(i).getSample().setRemain_time(1);
			}else if(1.0*i/COUNT_N<=Remain_Stand[1]) {
				all_nodes.get(i-1).getSample().setRemain_time(2);
			}else {
				all_nodes.get(i-1).getSample().setRemain_time(3);
			}
		}
	}
	public void toSamplePart4_MachineLoad(GA G) {
		List<machine> all_machines = new ArrayList();
		for(int i=0;i<G.getOrder().getN();i++) {
			all_machines.add(this.getGant().getMachines().get(i));
		}
		for(Job J:G.getOrder().getJobs()) {
			for(stage S:J.getStages()) {
				for(capableMachine m:S.getCapableMachines()) {
					int id = m.getMachineId();
					machine temp_m = all_machines.get(id-1);
					temp_m.setCapable_stage_count(temp_m.getCapable_stage_count()+1);
				}
			}
		}
		//ð�����򣬽���������
		//ð������
		for(int i =0 ; i<G.getOrder().getN() ; i++) { 
            for(int j=0 ; j<G.getOrder().getN()-1-i ; j++) {  
                if(all_machines.get(j).getCapable_stage_count()>all_machines.get(j+1).getCapable_stage_count()) {
                	Collections.swap(all_machines,j,j+1);
                }
            }    
        }		
		int machine_size = all_machines.size();
		for(int i=1;i<=machine_size;i++) {
			if(1.0*i/machine_size<=0.5) {
				all_machines.get(i-1).setMachineLoad(0);
			}else {
				all_machines.get(i-1).setMachineLoad(1);
			}
		}
		//���ù������ȼ�������ù�����һ���ڹؼ������ϣ���ù�����������ء�
		int Job_ptr=0,Stage_ptr=0;
		for(List<node> J:this.getSequence().getJob_process()) {
			Stage_ptr=0;
			for(int i=1;i<J.size();i++) {
				node N=J.get(i);
				stage S = G.getOrder().getJobs().get(Job_ptr).getStages().get(Stage_ptr);
				for(capableMachine m:S.getCapableMachines()) {
					int machineLoad = all_machines.get(m.getMachineId()-1).getMachineLoad();
					if(machineLoad ==0) {
						//�ù���������Ḻ�������ϼӹ�
						N.getSample().setMachine_load_light(N.getSample().getMachine_load_light()+1);
					}else {
						//�ù���������Ḻ�������ϼӹ�
						N.getSample().setMachine_load_heavy(N.getSample().getMachine_load_heavy()+1);
					}
				}
				Stage_ptr++;
			}
			Job_ptr++;
		}
	}
	public void toSamplePart5_Label(GA G,List<node> all_nodes,int COUNT_O) {
		for(node N:all_nodes) {
			if(N.getStartTime()==0&&N.getFinishTime()==0)
				continue;
			int n_stage = N.getStage_id();
			int n_rank=0;
			int [] C = this.getChromosome().getStageSequence();
			int [] Job_count = new int[G.getOrder().getM()];
			for(int i=0;i<C.length;i++) {
				int c=C[i];
				
				Job_count[c-1]++;
				if(c==N.getJob_id() && Job_count[c-1]==N.getStage_id()) {
					int stage_ptr=i+1;
//					System.out.println(stage_ptr);
					int priority = -1;
					if(1.0*stage_ptr/COUNT_O<=1.0/6) {
						priority=1;
					}else if(1.0*stage_ptr/COUNT_O<=2.0/6) {
						priority=2;
					}else if(1.0*stage_ptr/COUNT_O<=3.0/6) {
						priority=3;
					}else if(1.0*stage_ptr/COUNT_O<=4.0/6) {
						priority=4;
					}else if(1.0*stage_ptr/COUNT_O<=5.0/6) {
						priority=5;
					}else if(1.0*stage_ptr/COUNT_O<=6.0/6) {
						priority=6;
					}
					N.getSample().setChromosome_index(stage_ptr);;
					N.getSample().setPriority(priority);
					break;
				}
			}
		}
	}
	public void toNeuralNetwork2_part1_sort(GA G,List<node> all_nodes) {
		for(List<node> J: G.getCurPop().getBestIndividual().getSequence().getJob_process()) {
			for(node N: J) {
				if(N.getFinishTime()==0&&N.getStartTime()==0)
					continue;
				all_nodes.add(N);
			}
		}
		final int COUNT_N = all_nodes.size();
		//ð����������node����sample_priority����
		for(int i =0 ; i<COUNT_N ; i++) { 
            for(int j=0 ; j<COUNT_N-1-i ; j++) {  
                if(all_nodes.get(j).getSample().getPriority()>all_nodes.get(j+1).getSample().getPriority()) {
                	Collections.swap(all_nodes,j,j+1);
            }
            }    
        }
	}
	public String toNeuralNetwork2_part2_tolines(GA G,List<node> all_nodes) {
		String lines="";
		node N1 = null;
		node N2 = null;
		for(node N:all_nodes) {
			if(N.getFinishTime()==0 &&N.getStartTime()==0) {
				continue;
			}
			if(N1 ==null) {
				N1 = N;
			}else {
				N2 = N;
				if(N1.getSample().getPriority()!=N2.getSample().getPriority()) {
					//���������ȼ������
					N1=N2;
					N2=null;
				}else {
					//����N1��N2�����ȼ��������Ƚ�����
					String line = "";
					//�������OperationProcessTime,Operation,RemainTime,Light,Heavy
					if(N1.getCost()>N2.getCost()) {
						line+="1,0,0,";
					}else if(N1.getCost()==N2.getCost()) {
						line+="0,1,0,";
					}else if(N1.getCost()<N2.getCost()) {
						line+="0,0,1,";
					}
					if(N1.getStage_id()>N2.getStage_id()) {
						line+="1,0,0,";
					}else if(N1.getStage_id()==N2.getStage_id()) {
						line+="0,1,0,";
					}else if(N1.getStage_id()<N2.getStage_id()) {
						line+="0,0,1,";
					}
					if(N1.getRemain_time()>N2.getRemain_time()) {
						line+="1,0,0,";
					}else if(N1.getRemain_time()==N2.getRemain_time()) {
						line+="0,1,0,";
					}else if(N1.getRemain_time()<N2.getRemain_time()) {
						line+="0,0,1,";
					}
					if(N1.getSample().getMachine_load_light()>N2.getSample().getMachine_load_light()) {
						line+="1,0,0,";
					}else if(N1.getSample().getMachine_load_light()==N2.getSample().getMachine_load_light()) {
						line+="0,1,0,";
					}else if(N1.getSample().getMachine_load_light()<N2.getSample().getMachine_load_light()) {
						line+="0,0,1,";
					}
					if(N1.getSample().getMachine_load_heavy()>N2.getSample().getMachine_load_heavy()) {
						line+="1,0,0,";
					}else if(N1.getSample().getMachine_load_heavy()==N2.getSample().getMachine_load_heavy()) {
						line+="0,1,0,";
					}else if(N1.getSample().getMachine_load_heavy()<N2.getSample().getMachine_load_heavy()) {
						line+="0,0,1,";
					}
					//��ǩ�����N1����N2����ȡ1������ȡ0
					if(N1.getSample().getChromosome_index()<N2.getSample().getChromosome_index()) {
						line+="1\n";
					}else {
						line+="0\n";
					}
					lines+=line;
					N1=null;
					N2=null;
				}
			}
		}
		return lines;
	}
	public void toNeuralNetwork2(GA G) {
//		String titles = "OperationProcessTime:Greater,OperationProcessTime:Equal,OperationProcessTime:Less,";
//		outputs+="Operation:Greater,Operation:Equal,Operation:Less,";
//		outputs+="RemainingTime:Short,RemainingTime:Middle,RemainingTime:Long,";
//		outputs+="MachineLoadLight:Greater,MachineLoadLight:Equal,MachineLoadLight:Less,";
//		outputs+="MachineLoadHeavy:Greater,MachineLoadHeavy:Equal,MachineLoadHeavy:Less,";
//		outputs+="Priority:higher\n";
		String titles="";
		List<node> all_nodes = new ArrayList();
		toNeuralNetwork2_part1_sort(G,all_nodes);
		String lines = toNeuralNetwork2_part2_tolines(G,all_nodes);
		String outputs=titles+lines;
		try (BufferedWriter out = new BufferedWriter(new FileWriter(new File("network2.csv"),true))) {
	        String[] tokens= outputs.split("\n");
			for (String token:tokens) {
	          out.write(String.format(token));
	          out.write(System.lineSeparator());
	        }
	        out.flush();
	    }catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void toSampleWithMachineSelection(GA G,String file_path,boolean title) {
		//�ȶ�λ��˳�����
		final int COUNT_O=G.getOrder().getOperationCount();
		toSamplePart1_Location(G);
		//�ٶԹ���cost���з���,�㷨˼·������һ�����飬Ȼ����cost���򣬾Ϳ��Է�����
		List<node> all_nodes = new ArrayList();
		for(List<node> J:this.getSequence().getJob_process()) {
			for(node N:J) {
				all_nodes.add(N);
			}
		}
		final int COUNT_N = all_nodes.size();
		toSamplePart2_Cost(G,all_nodes,COUNT_O,COUNT_N);
		//�ڶԹ���ʣ��ʱ���������,�ȼ���ʣ��ʱ�䣬���������
		toSamplePart3_RemainTime(G,all_nodes,COUNT_O,COUNT_N);
		//�������������￼�ǻ����Ͽɼӹ��Ĺ����������ɼӹ�����Խ�࣬Խ�����ǹؼ�����������Խ�ء�
		toSamplePart4_MachineLoad(G);
		//��������������ϣ����������������ǩ
		toSamplePart5_Label(G,all_nodes,COUNT_O);
		//�������������ض���ʽд�������ļ���
		//������:��ҵ��
		//�����������1�������2�������3�������4���ӹ�ʱ��1���ӹ�ʱ��2���ӹ�ʱ��3��ʣ��ӹ�ʱ��1��ʣ��ӹ�ʱ��2��ʣ��ӹ�ʱ��3��
		//���������ڸ���С�����ϼӹ������ڸ����ػ����ϼӹ���
		//��ǩ���ӹ����ȼ���1��2��3��4��5��6��
		String output="";
		if(title) {
			output="Job_ID,"
				+ "Operation:First,Operation:Middle,Operation:Later,Operation:Last,"
				+ "ProcessTime:Short,ProcessTime:Middle,ProcessTime:Long,"
				+ "RemainingTime:Short,RemainingTime:Middle,RemainingTime:Long,"
				+ "MachineLoadlight,MachineLoadHeavy,"
				+ "OperationPriority,MachinePriority\n";
		}	
		for(node N:all_nodes) {
			if(N.getFinishTime()==0&&N.getStartTime()==0) {
				continue;
			}
			String line = N.getJob_id()+",";
			if(N.getSample().getLocation()==1) {
				line+="1,0,0,0,";
			}else if(N.getSample().getLocation()==2) {
				line+="0,1,0,0,";
			}else if(N.getSample().getLocation()==3) {
				line+="0,0,1,0,";
			}else if(N.getSample().getLocation()==4) {
				line+="0,0,0,1,";
			}
			if(N.getSample().getCost()==1) {
				line+="1,0,0,";
			}else if(N.getSample().getCost()==2) {
				line+="0,1,0,";
			}else if(N.getSample().getCost()==3) {
				line+="0,0,1,";
			}
			if(N.getSample().getRemain_time()==1) {
				line+="1,0,0,";
			}else if(N.getSample().getRemain_time()==2) {
				line+="0,1,0,";
			}else if(N.getSample().getRemain_time()==3) {
				line+="0,0,1,";
			}
			line+=N.getSample().getMachine_load_light()+",";
			line+=N.getSample().getMachine_load_heavy()+",";
			int Machine_id = N.getMachine_id();
			machine Machine = this.getGant().getMachines().get(Machine_id-1);
			line+=N.getSample().getPriority()+","+ Machine.getMachineLoad()+"\n";
			output+=line;
		}
		String outputs= output;
		try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(file_path),true))) {
	        String[] tokens= outputs.split("\n");
			for (String token:tokens) {
	          out.write(String.format(token));
	          out.write(System.lineSeparator());
	        }
	        out.flush();
	    }catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void toSample(GA G,String file_path) {
		//�ȶ�λ��˳�����
		final int COUNT_O=G.getOrder().getOperationCount();
		toSamplePart1_Location(G);
		//�ٶԹ���cost���з���,�㷨˼·������һ�����飬Ȼ����cost���򣬾Ϳ��Է�����
		List<node> all_nodes = new ArrayList();
		for(List<node> J:this.getSequence().getJob_process()) {
			for(node N:J) {
				all_nodes.add(N);
			}
		}
		final int COUNT_N = all_nodes.size();
		toSamplePart2_Cost(G,all_nodes,COUNT_O,COUNT_N);
		//�ڶԹ���ʣ��ʱ���������,�ȼ���ʣ��ʱ�䣬���������
		toSamplePart3_RemainTime(G,all_nodes,COUNT_O,COUNT_N);
		//�������������￼�ǻ����Ͽɼӹ��Ĺ����������ɼӹ�����Խ�࣬Խ�����ǹؼ�����������Խ�ء�
		toSamplePart4_MachineLoad(G);
		//��������������ϣ����������������ǩ
		toSamplePart5_Label(G,all_nodes,COUNT_O);
		//�������������ض���ʽд�������ļ���
		//������:��ҵ��
		//�����������1�������2�������3�������4���ӹ�ʱ��1���ӹ�ʱ��2���ӹ�ʱ��3��ʣ��ӹ�ʱ��1��ʣ��ӹ�ʱ��2��ʣ��ӹ�ʱ��3��
		//���������ڸ���С�����ϼӹ������ڸ����ػ����ϼӹ���
		//��ǩ���ӹ����ȼ���1��2��3��4��5��6��
		String output="Job_ID,"
				+ "Operation:First,Operation:Middle,Operation:Later,Operation:Last,"
				+ "ProcessTime:Short,ProcessTime:Middle,ProcessTime:Long,"
				+ "RemainingTime:Short,RemainingTime:Middle,RemainingTime:Long,"
				+ "MachineLoadlight,MachineLoadHeavy,"
				+ "OperationPriority\n";
//		String output="";
		for(node N:all_nodes) {
			if(N.getFinishTime()==0&&N.getStartTime()==0) {
				continue;
			}
			String line = N.getJob_id()+",";
			if(N.getSample().getLocation()==1) {
				line+="1,0,0,0,";
			}else if(N.getSample().getLocation()==2) {
				line+="0,1,0,0,";
			}else if(N.getSample().getLocation()==3) {
				line+="0,0,1,0,";
			}else if(N.getSample().getLocation()==4) {
				line+="0,0,0,1,";
			}
			if(N.getSample().getCost()==1) {
				line+="1,0,0,";
			}else if(N.getSample().getCost()==2) {
				line+="0,1,0,";
			}else if(N.getSample().getCost()==3) {
				line+="0,0,1,";
			}
			if(N.getSample().getRemain_time()==1) {
				line+="1,0,0,";
			}else if(N.getSample().getRemain_time()==2) {
				line+="0,1,0,";
			}else if(N.getSample().getRemain_time()==3) {
				line+="0,0,1,";
			}
			line+=N.getSample().getMachine_load_light()+",";
			line+=N.getSample().getMachine_load_heavy()+",";
			int Machine_id = N.getMachine_id();
			machine Machine = this.getGant().getMachines().get(Machine_id-1);
			line+=N.getSample().getPriority()+"\n";
			output+=line;
		}
		String outputs= output;
		try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(file_path),true))) {
	        String[] tokens= outputs.split("\n");
			for (String token:tokens) {
	          out.write(String.format(token));
	          out.write(System.lineSeparator());
	        }
	        out.flush();
	    }catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	public void toSample2(GA G,String file_path) {
		//�ȶ�λ��˳�����
		final int COUNT_O=G.getOrder().getOperationCount();
		toSamplePart1_Location(G);
		//�ٶԹ���cost���з���,�㷨˼·������һ�����飬Ȼ����cost���򣬾Ϳ��Է�����
		List<node> all_nodes = new ArrayList();
		for(List<node> J:this.getSequence().getJob_process()) {
			for(node N:J) {
				all_nodes.add(N);
			}
		}
		final int COUNT_N = all_nodes.size();
		toSamplePart2_Cost(G,all_nodes,COUNT_O,COUNT_N);
		//�ڶԹ���ʣ��ʱ���������,�ȼ���ʣ��ʱ�䣬���������
		toSamplePart3_RemainTime(G,all_nodes,COUNT_O,COUNT_N);
		//�������������￼�ǻ����Ͽɼӹ��Ĺ����������ɼӹ�����Խ�࣬Խ�����ǹؼ�����������Խ�ء�
		toSamplePart4_MachineLoad(G);
		//��������������ϣ����������������ǩ
		toSamplePart5_Label(G,all_nodes,COUNT_O);
		//�������������ض���ʽд�������ļ���
		//������:��ҵ��
		//�����������1�������2�������3�������4���ӹ�ʱ��1���ӹ�ʱ��2���ӹ�ʱ��3��ʣ��ӹ�ʱ��1��ʣ��ӹ�ʱ��2��ʣ��ӹ�ʱ��3��
		//���������ڸ���С�����ϼӹ������ڸ����ػ����ϼӹ���
		//��ǩ���ӹ����ȼ���1��2��3��4��5��6��
		String output="Job_ID,"
				+ "Operation:First,Operation:Middle,Operation:Later,Operation:Last,"
				+ "ProcessTime:Short,ProcessTime:Middle,ProcessTime:Long,"
				+ "RemainingTime:Short,RemainingTime:Middle,RemainingTime:Long,"
				+ "MachineLoadlight,MachineLoadHeavy,"
				+ "Priority,"
				+ "Operation,ProcessTime,ReaminingTime,MachineLoadLight,MachineLoadHeavy\n";
		
//		String output="";
		for(node N:all_nodes) {
			if(N.getFinishTime()==0&&N.getStartTime()==0) {
				continue;
			}
			String line = N.getJob_id()+",";
			if(N.getSample().getLocation()==1) {
				line+="1,0,0,0,";
			}else if(N.getSample().getLocation()==2) {
				line+="0,1,0,0,";
			}else if(N.getSample().getLocation()==3) {
				line+="0,0,1,0,";
			}else if(N.getSample().getLocation()==4) {
				line+="0,0,0,1,";
			}
			if(N.getSample().getCost()==1) {
				line+="1,0,0,";
			}else if(N.getSample().getCost()==2) {
				line+="0,1,0,";
			}else if(N.getSample().getCost()==3) {
				line+="0,0,1,";
			}
			if(N.getSample().getRemain_time()==1) {
				line+="1,0,0,";
			}else if(N.getSample().getRemain_time()==2) {
				line+="0,1,0,";
			}else if(N.getSample().getRemain_time()==3) {
				line+="0,0,1,";
			}
			line+=N.getSample().getMachine_load_light()+",";
			line+=N.getSample().getMachine_load_heavy()+",";
			line+=N.getSample().getPriority()+",";
			line+=N.getStage_id()+","+N.getCost()+","+N.getRemain_time()+","
			+N.getSample().getMachine_load_light()+","+N.getSample().getMachine_load_heavy()+"\n";
			output+=line;
		}
		String outputs= output;
		try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(file_path),true))) {
	        String[] tokens= outputs.split("\n");
			for (String token:tokens) {
	          out.write(String.format(token));
	          out.write(System.lineSeparator());
	        }
	        out.flush();
	    }catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
class gant{
	private List<machine> machines;

	public List<machine> getMachines() {
		return machines;
	}

	public gant setMachines(List<machine> machines) {
		this.machines = machines;
		return this;
	}
	public String toString() {
		String output="";
		for(machine m:this.getMachines()) {
			String line = m.getMachineName()+"  ";
			for(node n:m.getNodes()) {
				line+=n.getStartTime()+" "+n.getFinishTime()+" ";
			}
			output+=line+"\n";
		}
		return output;
	}
}
class node{
	public int getJob_id() {
		return job_id;
	}
	public node setJob_id(int job_id) {
		this.job_id = job_id;
		return this;
	}
	public int getStage_id() {
		return stage_id;
	}
	public node setStage_id(int stage_id) {
		this.stage_id = stage_id;
		return this;
	}
	public int getMachine_id() {
		return machine_id;
	}
	public node setMachine_id(int machine_id) {
		this.machine_id = machine_id;
		return this;
	}
	public int getStartTime() {
		return startTime;
	}
	public node setStartTime(int startTime) {
		this.startTime = startTime;
		return this;
	}
	public int getFinishTime() {
		return finishTime;
	}
	public node setFinishTime(int finishTime) {
		this.finishTime = finishTime;
		return this;
	}
	private int job_id;
	private int stage_id;
	private int machine_id;
	private int startTime;
	private int finishTime;
	private BP_Sample sample;
	private int remain_time;
	public int getRemain_time() {
		return remain_time;
	}
	public void setRemain_time(int remain_time) {
		this.remain_time = remain_time;
	}
	public BP_Sample getSample() {
		if(this.sample==null) {
			BP_Sample s=new BP_Sample();
			
			this.setSample(s);
		}
		return sample;
	}
	public void setSample(BP_Sample sample) {
		this.sample = sample;
	}
	public int getCost() {
		return this.getFinishTime()-this.getStartTime();
	}
}
class sequence{
	private List<List<node>> job_process;

	public List<List<node>> getJob_process() {
		return job_process;
	}
	
	public void setJob_process(List<List<node>> job_process) {
		this.job_process = job_process;
	}
	public String toString() {
		String output = "";
		int count=0;
		for(List<node> job:this.getJob_process()) {
			count++;
			String line="Job"+count+"  ";
			for(node n:job) {
				line+= " "+n.getStartTime()+" "+n.getFinishTime()+" ";
			}
			output+=line+"\n";
			
		}
		return output;
	}
	public String toFile() {
		String output="nodeID,JobID,StartTime,FinishTime,MachineID\n";
		int nodeID=0;
		int jobID=0;
		for(List<node> job:this.getJob_process()) {
			jobID++;
			for(node n:job) {
				nodeID++;
				String line = nodeID+","+jobID+","+n.getStartTime()+","+n.getFinishTime()+","+n.getMachine_id()+"\n";
				output+=line;
			}
		}
		return output;
	}
}
class machine{
	private int machineID;
	private String machineName;
	private List<node> nodes;
	private int machineLoad;
	private int capable_stage_count=0;
	public int getCapable_stage_count() {
		return capable_stage_count;
	}
	public void setCapable_stage_count(int capable_stage_count) {
		this.capable_stage_count = capable_stage_count;
	}
	public int getMachineLoad() {
		return machineLoad;
	}
	public void setMachineLoad(int machineLoad) {
		this.machineLoad = machineLoad;
	}
	public int getMachineID() {
		return machineID;
	}
	public void setMachineID(int machineID) {
		this.machineID = machineID;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public List<node> getNodes() {
		return nodes;
	}
	public void setNodes(List<node> nodes) {
		this.nodes = nodes;
	}
}