import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GAoperations {
	public static void select(GA G) {
		GAoperations.championships(G,2);
	}
	public static void championships(GA G,int N) {
		pop newPop = new pop().setPopSize(G.getPopSize()).setIndividuals(new ArrayList<individual>());;
		pop oldPop = G.getCurPop();
		G.setCurPop(newPop);
		int popSize = G.getPopSize();
		for(int i=0;i<popSize;i++) {
			List<Integer> set = new ArrayList();
			List<individual> candidates = new ArrayList();
			int randoms[] = new int[N];
			for(int j=0;j<popSize;j++) {
				set.add(j);
			}
			for(int j=0;j<N;j++) {
				randoms[j]=set.remove((int)(Math.random()*set.size()));
			}
			for(int j=0;j<N;j++) {
				candidates.add(oldPop.getIndividuals().get(randoms[j]));
			}
			candidates.sort(Comparator.naturalOrder());
			individual champion = candidates.get(0);
			chromosome C = new chromosome();
			C.setMachineSelection(champion.getChromosome().getMachineSelection().clone());
			C.setStageSequence(champion.getChromosome().getStageSequence().clone());
			individual I = new individual().setChromosome(C);
			I.decode(G);
			newPop.getIndividuals().add(I);
		}
		
		
	}
	public static void mutation(GA G) {
		for(individual i:G.getCurPop().getIndividuals()) {
			double r = Math.random();
			if(r<G.getMutationRate())
				GAoperations.mutation(G,i);
		}
	}
	public static void crossOver(GA G) {
		pop Pop = G.getCurPop();
		individual I1=null,I2=null;
		for(individual i:Pop.getIndividuals()) {
			double r = Math.random();
			
			if(r<G.getCrossoevrRate()) {
				if(I1==null) {
					I1 = i;
				}else {
					I2 = i;
					GAoperations.crossOver2(G, I1, I2);
					I1=null;
					I2=null;
				}
			}
		}
	}
	public static void init(GA G) {
		pop Pop = G.getCurPop();
		double accumulation_P[] = new double[3];
		accumulation_P[0]=0;accumulation_P[1]=G.getPG();accumulation_P[2]=G.getPG()+G.getPL();
		List<individual> individuals = new ArrayList();
		for(int i =0 ;i<G.getPopSize();i++) {
			individual tempIndividual = null;
			double r = Math.random();
			if(r<accumulation_P[1]) {
				//randomInit
				tempIndividual = GlobalInit(G);
			}else if(r<accumulation_P[2]) {
				//localInit
				tempIndividual = LocalInit(G);
			}else if(r<1) {
				//GlobalInit
				tempIndividual = randomInit(G);
			}
			
			individuals.add(tempIndividual);
		}
		Pop.setIndividuals(individuals);
	}
	public static individual randomInit(GA G) {
		individual result = new individual();
		order o = G.getOrder();
//		order o = new order();
//		o.setJobs(o.read_file("MK01.txt"));
		//得到工序数量，并生成机器选择数组与序列顺序数组。添加到染色体中。
		int COUNT = o.getOperationCount();
		int [] machineSelection = new int[COUNT];
		int [] stageSequence = new int[COUNT];
		chromosome Chromo = new chromosome();
		Chromo.setMachineSelection(machineSelection);
		Chromo.setStageSequence(stageSequence);
		result.setChromosome(Chromo);
		//重复以下操作直到所有工序都被选择完毕
		//Job firstJob = o.getJobs().get(0);
		//stage firstJobFirstStage = firstJob.getStages().get(0);
		//int r = (int) (1+Math.random()*firstJobFirstStage.getCapableMachines().size());
		//machineSelection[0]=r;
		int count=0;
		for(int i=0;i<o.getM();i++) {
			Job tempJob = o.getJobs().get(i);
			for(int j=0;j<tempJob.getStages().size();j++) {
				stage tempStage = tempJob.getStages().get(j);
				int r = (int)(1+Math.random()*tempStage.getCapableMachines().size());
				machineSelection[count]=r;
				count++;
			}
		}
		//随机产生工序部分染色体,首先生成类似111，222，33，444，555的染色体序列,然后将其打乱
		count=0;
		for(int i=0;i<o.getM();i++) {
			Job tempJob = o.getJobs().get(i);
			for(int j=0;j<tempJob.getStages().size();j++) {
				stageSequence[count]=(i+1);
				count++;
			}
		}
		//shuffle,遍历数组，每次生成一个随机位置与当前位置上的数字交换
		for(int i=0;i<COUNT;i++) {
			int r = (int) (Math.random()*COUNT);
			int temp = stageSequence[r];
			stageSequence[r]=stageSequence[i];
			stageSequence[i]=temp;
		}
		//至此，染色体生成完毕，调用decode进行解码
		result.decode(G);
		
		//解码完毕，个体产生完毕。
		return result;	
	}
	public static void crossOver(GA G,individual I1,individual I2) {
//		order o = new order();
//		o.setJobs(o.read_file("MK01.txt"));
//		System.out
		order o = G.getOrder();
		int T0 = o.getOperationCount();
		int R = (int)(Math.random()*T0);
		ArrayList<Integer> selectSet = new ArrayList();
		for(int i=0;i<o.getOperationCount();i++) {
			selectSet.add(i);
		}
		int[] randoms = new int[R];
		for(int i=0;i<R;i++) {
			int r = (int) (Math.random()*selectSet.size());
			randoms[i] = selectSet.remove(r);
		}
		//randoms中每一个下标对应的基因位上的机器选择部分基因进行交换。
		int[] MS1 = I1.getChromosome().getMachineSelection();
		int[] MS2 = I2.getChromosome().getMachineSelection();
		for(int i=0;i<R;i++) {
			int index = randoms[i];
			int temp = MS1[index];
			MS1[index] = MS2[index];
			MS2[index] = temp;
		}
		//工序排序部分，POX 基于工件优先顺序的交叉
		List<Integer> S1 = new ArrayList();
		int T1 = (int)(1+Math.random()*(o.getM()-1));
		//随机将Job划分为S1（T1个元素），S2（M-T1个元素）
		for(int i=1;i<=T1;i++) {
			S1.add(i);
		}
		List<Integer> S2 = new ArrayList();
		for(int i=0;i<T1;i++) {
			int r = (int)(Math.random()*S1.size());
			S2.add(S1.remove( r));
		}
		int [] OS1 = I1.getChromosome().getStageSequence();
		int [] OS2 = I2.getChromosome().getStageSequence();
		final int COUNT = o.getOperationCount();
		int [] C1 = new int[COUNT];
		int [] C2 = new int[COUNT];
		for(int i=0;i<COUNT;i++) {
			C1[i]=0;
			C2[i]=0;
		}
		int ptr1=0,ptr2=0;
		for(int i=0;i<COUNT;i++) {
			if(S1.contains(OS1[i])) {
				C1[i]=OS1[i];
			}else {
				while(S1.contains(OS2[ptr1])) {
					ptr1++;
				}
				C1[i]=OS2[ptr1];
				ptr1++;
			}
			if(S2.contains(OS2[i])) {
				C2[i]=OS2[i];
			}else {
				while(S2.contains(OS1[ptr2])) {
					ptr2++;
				}
				C2[i]=OS1[ptr2];
				ptr2++;
			}
		}
		I1.getChromosome().setStageSequence(C1);
		I2.getChromosome().setStageSequence(C2);
		I1.decode(G);
		I2.decode(G);
//		for(int i=0;i<COUNT;i++) {
//			System.out.println("S1["+i+"]:"+OS1[i]+"  S2["+i+"]:"+OS2[i]);
//			System.out.println("C1["+i+"]:"+C1[i]+"  C2["+i+"]:"+C2[i]);
//		}
//		
//		
//		System.out.println(S1);
//		System.out.println(S2);
		
	}
	public static void crossOver2(GA G,individual I1,individual I2) {
		//引入避免近亲繁殖概念
//		order o = new order();
//		o.setJobs(o.read_file("MK01.txt"));
//		System.out
		order o = G.getOrder();
		int T0 = o.getOperationCount();
		int R = (int)(Math.random()*T0);
		ArrayList<Integer> selectSet = new ArrayList();
		for(int i=0;i<o.getOperationCount();i++) {
			selectSet.add(i);
		}
		int[] randoms = new int[R];
		for(int i=0;i<R;i++) {
			int r = (int) (Math.random()*selectSet.size());
			randoms[i] = selectSet.remove(r);
		}
		//randoms中每一个下标对应的基因位上的机器选择部分基因进行交换。
		int[] MS1 = I1.getChromosome().getMachineSelection();
		int[] MS2 = I2.getChromosome().getMachineSelection();
		for(int i=0;i<R;i++) {
			int index = randoms[i];
			int temp = MS1[index];
			MS1[index] = MS2[index];
			MS2[index] = temp;
		}
		//工序排序部分，POX 基于工件优先顺序的交叉
		List<Integer> S1 = new ArrayList();
		int T1 = (int)(1+Math.random()*(o.getM()-1));
		//随机将Job划分为S1（T1个元素），S2（M-T1个元素）
		for(int i=1;i<=T1;i++) {
			S1.add(i);
		}
		List<Integer> S2 = new ArrayList();
		for(int i=0;i<T1;i++) {
			int r = (int)(Math.random()*S1.size());
			S2.add(S1.remove( r));
		}
		int [] OS1 = I1.getChromosome().getStageSequence();
		int [] OS2 = I2.getChromosome().getStageSequence();
		final int COUNT = o.getOperationCount();
		int [] C1 = new int[COUNT];
		int [] C2 = new int[COUNT];
		for(int i=0;i<COUNT;i++) {
			C1[i]=0;
			C2[i]=0;
		}
		int ptr1=0,ptr2=0;
		for(int i=0;i<COUNT;i++) {
			if(S1.contains(OS1[i])) {
				C1[i]=OS1[i];
			}else {
				while(S1.contains(OS2[ptr1])) {
					ptr1++;
				}
				C1[i]=OS2[ptr1];
				ptr1++;
			}
			if(S2.contains(OS2[i])) {
				C2[i]=OS2[i];
			}else {
				while(S2.contains(OS1[ptr2])) {
					ptr2++;
				}
				C2[i]=OS1[ptr2];
				ptr2++;
			}
		}
		I1.getChromosome().setStageSequence(C1);
		I2.getChromosome().setStageSequence(C2);
		I1.decode(G);
		I2.decode(G);
		double similarity = 0;
		int [] I1MS = I1.getChromosome().getMachineSelection();
		int [] I1OS = I1.getChromosome().getMachineSelection();
		int [] I2MS = I2.getChromosome().getMachineSelection();
		int [] I2OS = I2.getChromosome().getMachineSelection();
		for(int i=0;i<o.getOperationCount();i++) {
			if(I1MS[i]==I2MS[i])
				similarity++;
			if(I1OS[i]==I2OS[i])
				similarity++;
		}
		similarity=similarity/o.getOperationCount()/2;
		if(similarity>=0.7) {
//			System.out.println("hello");
			I1=I1.getTotalCost()<I2.getTotalCost()?I1:I2;
			double r = Math.random();
			if(r<G.getPG())
				I2=GlobalInit(G);
			else if(r<G.getPG()+G.getPL())
				I2=LocalInit(G);
			else
				I2=randomInit(G);
			I2.decode(G);
		}
		
//		for(int i=0;i<COUNT;i++) {
//			System.out.println("S1["+i+"]:"+OS1[i]+"  S2["+i+"]:"+OS2[i]);
//			System.out.println("C1["+i+"]:"+C1[i]+"  C2["+i+"]:"+C2[i]);
//		}
//		
//		
//		System.out.println(S1);
//		System.out.println(S2);
		
	}
//	public static void mutation(GA G,individual I) {
//		I.decode(G);
//		int [] MS = I.getChromosome().getMachineSelection();
//		int [] MS_copy = MS.clone();
//		int [] OS = I.getChromosome().getStageSequence();
//		//MS变异
//		order o = G.getOrder();
////		order o = new order();
////		o.setJobs(o.read_file("MK01.txt"));
//		final int COUNT =  o.getOperationCount();
//		int R = (int) ((Math.random()*(COUNT-1))+1);
//		List<Integer> random_index = new ArrayList();
//		List<Integer> select_set = new ArrayList();
//		for(int i=0;i<o.getOperationCount();i++) 
//			select_set.add(i);
//		for(int i=0;i<R;i++) {
//			random_index.add(select_set.get((int)(Math.random()*select_set.size())));
//		}
//		random_index.sort(Comparator.naturalOrder());
//		int stage_ptr=0;
//		int random_ptr=0;
//		for(Job j:o.getJobs()) {
//			for(stage s:j.getStages()) {
//				if(random_index.get(random_ptr)==stage_ptr) {
//					int minCost = GA.INF;
//					int min_order=-1;
//					int order_count =0;
//					capableMachine min = null;
//					List<individual> tempIs = new ArrayList();
//					//我对算法改动了，尝试所有可能的机器，选一个最好的，结果没有显著提升
//					for(capableMachine m:s.getCapableMachines()) {
//						order_count++;	
//						int[] temp_MS=MS_copy.clone();
//						temp_MS[random_index.get(random_ptr)]=order_count;
//						individual tempI = new individual().setChromosome(new chromosome().setMachineSelection(temp_MS).setStageSequence(OS.clone()));
//						tempI.decode(G);
//						tempIs.add(tempI);
//					}
//					tempIs.sort(Comparator.naturalOrder());
//					MS_copy=tempIs.get(0).getChromosome().getMachineSelection();
//				}
//				stage_ptr++;
//			}
//		}
//		I.getChromosome().setMachineSelection(MS_copy);
//		I.decode(G);
//		//对于工序部分，采用基于邻域搜索变异方法
//		//1.产生R个基因位，random
//		
//		R = (int)(2+Math.random()*3);
//		List<Integer> selectSet = new ArrayList();
//		int[]randoms = new int[R];
//		for(int i=0;i<o.getOperationCount();i++) {
//			selectSet.add(i);
//		}
//		for(int i=0;i<R;i++) {
//			int r = (int) (Math.random()*selectSet.size());
////			System.out.println("selectset:"+selectSet.size()+" random.length:"+randoms.length);
//			randoms[i] = selectSet.remove(r);
//		}
//		//生成R个基因的全排列
//		int Full_Permutation_Count=1;
//		for(int i=1;i<=R;i++) {
//			Full_Permutation_Count*=i;
//		}
////		System.out.println("R:"+R+"  Full_Permutation_Count:"+Full_Permutation_Count);
//		int[][] Full_Permutation = new int[Full_Permutation_Count][R];
//		int[] OS_copy = OS.clone();
//		int[] elements = new int[R];
//		for(int i=0;i<R;i++) {
//			elements[i]=OS[randoms[i]];
//		}
//		arrange(Full_Permutation,elements,0,elements.length-1);
//		row=0;
//		//将这些基因按序填补上后，解码，找到最优个体。
//		
//		List<individual> candidates = new ArrayList();
//		for(int i=0;i<Full_Permutation_Count;i++) {
//			for(int j=0;j<R;j++) {
//				OS_copy[randoms[j]]=Full_Permutation[i][j];
//			}
//			for(int var3=0;var3<OS.length;var3++) {
//				if(OS[var3]!=OS_copy[var3]) {
////					System.out.println(var3);
//				}
//			}
//			individual tempI = new individual();
//			chromosome tempC = new chromosome();
//			tempC.setMachineSelection(MS);
//			tempC.setStageSequence(OS_copy);
//			tempI.setChromosome(tempC);
//			I.decode(G);
//			tempI.decode(G);
//			candidates.add(tempI);
//		}
//		candidates.sort(Comparator.naturalOrder());
//		I=candidates.get(0);
//	}
	public static void mutation(GA G,individual I) {
	//贪婪搜索
		I.decode(G);
		int [] MS = I.getChromosome().getMachineSelection();
		int [] MS_copy = MS.clone();
		int [] OS = I.getChromosome().getStageSequence();
		//MS变异
		order o = G.getOrder();
//		order o = new order();
//		o.setJobs(o.read_file("MK01.txt"));
		final int COUNT =  o.getOperationCount();
		int R = (int) ((Math.random()*(COUNT-1))+1);
		List<Integer> random_index = new ArrayList();
		List<Integer> select_set = new ArrayList();
		for(int i=0;i<o.getOperationCount();i++) 
			select_set.add(i);
		for(int i=0;i<R;i++) {
			random_index.add(select_set.get((int)(Math.random()*select_set.size())));
		}
		random_index.sort(Comparator.naturalOrder());
		int stage_ptr=0;
		int random_ptr=0;
		for(Job j:o.getJobs()) {
			for(stage s:j.getStages()) {
				if(random_index.get(random_ptr)==stage_ptr) {
					int minCost = GA.INF;
					int min_order=-1;
					int order_count =0;
					capableMachine min = null;
					for(capableMachine m:s.getCapableMachines()) {
						order_count++;
						if(m.getCostTime()<minCost) {
							minCost=m.getCostTime();
							min=m;
							min_order=order_count;
						}		
					}
					MS_copy[random_index.get(random_ptr)]=order_count;
				}
				stage_ptr++;
			}
		}
		I.getChromosome().setMachineSelection(MS_copy);
		I.decode(G);
		//对于工序部分，采用基于邻域搜索变异方法
		//1.产生R个基因位，random，为避免全排列过多，指数爆炸，将r限制在2-5内
		
		
		R = (int)(2+Math.random()*3);
		List<Integer> selectSet = new ArrayList();
		int[]randoms = new int[R];
		for(int i=0;i<o.getOperationCount();i++) {
			selectSet.add(i);
		}
		for(int i=0;i<R;i++) {
			int r = (int) (Math.random()*selectSet.size());
//			System.out.println("selectset:"+selectSet.size()+" random.length:"+randoms.length);
			randoms[i] = selectSet.remove(r);
		}
		//生成R个基因的全排列
		int Full_Permutation_Count=1;
		for(int i=1;i<=R;i++) {
			Full_Permutation_Count*=i;
		}
//		System.out.println("R:"+R+"  Full_Permutation_Count:"+Full_Permutation_Count);
		int[][] Full_Permutation = new int[Full_Permutation_Count][R];
		int[] OS_copy = OS.clone();
		int[] elements = new int[R];
		for(int i=0;i<R;i++) {
			elements[i]=OS[randoms[i]];
		}
		arrange(Full_Permutation,elements,0,elements.length-1);
		row=0;
		//将这些基因按序填补上后，解码，找到最优个体。
		
		List<individual> candidates = new ArrayList();
		for(int i=0;i<Full_Permutation_Count;i++) {
			for(int j=0;j<R;j++) {
				OS_copy[randoms[j]]=Full_Permutation[i][j];
			}
			for(int var3=0;var3<OS.length;var3++) {
				if(OS[var3]!=OS_copy[var3]) {
//					System.out.println(var3);
				}
			}
			individual tempI = new individual();
			chromosome tempC = new chromosome();
			tempC.setMachineSelection(MS);
			tempC.setStageSequence(OS_copy);
			tempI.setChromosome(tempC);
			I.decode(G);
			tempI.decode(G);
			candidates.add(tempI);
		}
		candidates.sort(Comparator.naturalOrder());
		I=candidates.get(0);
	}
	public static individual LocalInit(GA G) {
		order O = G.getOrder();
		individual I = new individual();
		int[] MS = new int[O.getOperationCount()];
		int[] OS = new int[O.getOperationCount()];
		int ptr_OS=0;
		int[] ptr_MS = new int[O.getJobs().size()];
		int count=0;
		int t=0;
		for(Job J:O.getJobs()) {
			ptr_MS[t]=count;
			for(stage S:J.getStages()) 
				count++;
			t++;
		}
		chromosome C = new chromosome().setMachineSelection(MS).setStageSequence(OS);
		I.setChromosome(C);
		//首先生成工件优先顺序JobOrder
		List<Integer> JobOrder=new ArrayList();
		List<Integer> tempSet = new ArrayList();
		/*与全局选择不同，工件顺序不随机，固定为1，2，3，……，M
		 * 那么疑点来了，这样的话初始种群多样性时不能保证的
		 * 种群中全局选择M个基因型，局部选择1个，随机r个，
		 * 那么初始种群中的表现型有M+1+r个，表现型是比较少的
		 * for(int i=1;i<=O.getM();i++)
			JobOrder.add(i);
		*/
		
		/* 
			所以我试试随机
		*/
		for(int i=1;i<=O.getM();i++)
			tempSet.add(i);
		for(int i=0;i<O.getM();i++)
			JobOrder.add(tempSet.remove((int)(tempSet.size()*Math.random())));
		
		//定义机器负载数组并初始化为0
		int[] Loads = new int[O.getN()];
		for(int load:Loads) load=0;
		
		//repeat
		for(int i=0;i<O.getM();i++) {
			Job curJob = O.getJobs().get(JobOrder.get(i)-1);
			//更新工序基因
			//局部选择时考虑每个机器单独的负载
			for(int load:Loads) load=0;
			for(int j=0;j<curJob.getStages().size();j++) {
				OS[ptr_OS]=JobOrder.get(i);
				ptr_OS++;
			}
			int stage_count=ptr_MS[JobOrder.get(i)-1];
			for(stage curStage:curJob.getStages()) {
				List<Integer> candidate_machine_ID = new ArrayList();
				List<Integer> tempLoads = new ArrayList();
				for(capableMachine curM:curStage.getCapableMachines()) {
					candidate_machine_ID.add(curM.getMachineId());
					tempLoads.add(Loads[curM.getMachineId()-1]+curM.getCostTime());
				}
				int minCost = GA.INF;
				int minMachine = -1;
				for(int j=0;j<tempLoads.size();j++) {
					if(minCost>tempLoads.get(j)) {
						minCost=tempLoads.get(j);
						minMachine = j+1;
					}
				}
				//更新负载
				Loads[candidate_machine_ID.get(minMachine-1)-1]=minCost;
				//minMachine 就是当前基因位上的值，现在要找到当前基因的下标，更新MS
				MS[stage_count]=minMachine;
				stage_count++;
				
			}
		}
		
		return I;
	}
	public static individual network_scheduling_with_MSPriority(GA G,int[] OS,int[] MS_Pri) {
		individual I = new individual();
		order O = G.getOrder();
		int ptr_OS=0;
		int[] MS = new int[O.getOperationCount()];
		int[] ptr_MS = new int[O.getJobs().size()];
		int count=0;
		int t=0;
		for(Job J:O.getJobs()) {
			ptr_MS[t]=count;
			for(stage S:J.getStages()) 
				count++;
			t++;
		}
		chromosome C = new chromosome().setMachineSelection(MS).setStageSequence(OS);
		I.setChromosome(C);
		//首先生成工件优先顺序JobOrder
		List<Integer> JobOrder=new ArrayList();
		List<Integer> tempSet = new ArrayList();
		for(int i=1;i<=O.getM();i++)
			tempSet.add(i);
		for(int i=0;i<O.getM();i++)
			JobOrder.add(tempSet.remove((int)(tempSet.size()*Math.random())));
		//定义机器负载数组并初始化为0
		int[] Loads = new int[O.getN()];
		for(int load:Loads) load=0;
		
		//repeat
		for(int i=0;i<O.getM();i++) {
			Job curJob = O.getJobs().get(JobOrder.get(i)-1);
			int stage_count=ptr_MS[JobOrder.get(i)-1];
			for(stage curStage:curJob.getStages()) {
				List<Integer> candidate_machine_ID = new ArrayList();
				List<Integer> tempLoads = new ArrayList();
					
				
				for(capableMachine curM:curStage.getCapableMachines()) {
					if(curM.getHeavy()==MS_Pri[stage_count]) {
						candidate_machine_ID.add(curM.getMachineId());
						tempLoads.add(Loads[curM.getMachineId()-1]+curM.getCostTime());
					}
					
				}
				if(candidate_machine_ID.size()==0) {
					//MS_Pri误分类，产生了非法解，直接按负载选择
					for(capableMachine curM:curStage.getCapableMachines()) {
						candidate_machine_ID.add(curM.getMachineId());
						tempLoads.add(Loads[curM.getMachineId()-1]+curM.getCostTime());		
					}
				}
				int minCost = GA.INF;
				int minMachine = -1;
				for(int j=0;j<tempLoads.size();j++) {
					if(minCost>tempLoads.get(j)) {
						minCost=tempLoads.get(j);
						minMachine = j+1;
					}
				}
				//更新负载
				Loads[candidate_machine_ID.get(minMachine-1)-1]=minCost;
				//minMachine 就是当前基因位上的值，现在要找到当前基因的下标，更新MS
				MS[stage_count]=minMachine;
				stage_count++;
				
			}
		}
		
		return I;
	}
	public static individual network_scheduling(GA G,int [] OS) {
		individual I = new individual();
		order O = G.getOrder();
		int[] MS = new int[O.getOperationCount()];
		int ptr_OS=0;
		int[] ptr_MS = new int[O.getJobs().size()];
		int count=0;
		int t=0;
		for(Job J:O.getJobs()) {
			ptr_MS[t]=count;
			for(stage S:J.getStages()) 
				count++;
			t++;
		}
		chromosome C = new chromosome().setMachineSelection(MS).setStageSequence(OS);
		I.setChromosome(C);
		//首先生成工件优先顺序JobOrder
		List<Integer> JobOrder=new ArrayList();
		List<Integer> tempSet = new ArrayList();
		for(int i=1;i<=O.getM();i++)
			tempSet.add(i);
		for(int i=0;i<O.getM();i++)
			JobOrder.add(tempSet.remove((int)(tempSet.size()*Math.random())));
		//定义机器负载数组并初始化为0
		int[] Loads = new int[O.getN()];
		for(int load:Loads) load=0;
		
		//repeat
		for(int i=0;i<O.getM();i++) {
			Job curJob = O.getJobs().get(JobOrder.get(i)-1);
			//更新工序基因
			//指定了工序所以不需要自己初始化了。
//			for(int j=0;j<curJob.getStages().size();j++) {
//				OS[ptr_OS]=JobOrder.get(i);
//				ptr_OS++;
//			}
			int stage_count=ptr_MS[JobOrder.get(i)-1];
			for(stage curStage:curJob.getStages()) {
				List<Integer> candidate_machine_ID = new ArrayList();
				List<Integer> tempLoads = new ArrayList();
				for(capableMachine curM:curStage.getCapableMachines()) {
					candidate_machine_ID.add(curM.getMachineId());
					tempLoads.add(Loads[curM.getMachineId()-1]+curM.getCostTime());
				}
				int minCost = GA.INF;
				int minMachine = -1;
				for(int j=0;j<tempLoads.size();j++) {
					if(minCost>tempLoads.get(j)) {
						minCost=tempLoads.get(j);
						minMachine = j+1;
					}
				}
				//更新负载
				Loads[candidate_machine_ID.get(minMachine-1)-1]=minCost;
				//minMachine 就是当前基因位上的值，现在要找到当前基因的下标，更新MS
				MS[stage_count]=minMachine;
				stage_count++;
				
			}
		}
		
		return I;
	}
	public static individual GlobalInit(GA G) {
		order O = G.getOrder();
		individual I = new individual();
		int[] MS = new int[O.getOperationCount()];
		int[] OS = new int[O.getOperationCount()];
		int ptr_OS=0;
		int[] ptr_MS = new int[O.getJobs().size()];
		int count=0;
		int t=0;
		for(Job J:O.getJobs()) {
			ptr_MS[t]=count;
			for(stage S:J.getStages()) 
				count++;
			t++;
		}
		chromosome C = new chromosome().setMachineSelection(MS).setStageSequence(OS);
		I.setChromosome(C);
		//首先生成工件优先顺序JobOrder
		List<Integer> JobOrder=new ArrayList();
		List<Integer> tempSet = new ArrayList();
		for(int i=1;i<=O.getM();i++)
			tempSet.add(i);
		for(int i=0;i<O.getM();i++)
			JobOrder.add(tempSet.remove((int)(tempSet.size()*Math.random())));
		//定义机器负载数组并初始化为0
		int[] Loads = new int[O.getN()];
		for(int load:Loads) load=0;
		
		//repeat
		for(int i=0;i<O.getM();i++) {
			Job curJob = O.getJobs().get(JobOrder.get(i)-1);
			//更新工序基因
			for(int j=0;j<curJob.getStages().size();j++) {
				OS[ptr_OS]=JobOrder.get(i);
				ptr_OS++;
			}
			int stage_count=ptr_MS[JobOrder.get(i)-1];
			for(stage curStage:curJob.getStages()) {
				List<Integer> candidate_machine_ID = new ArrayList();
				List<Integer> tempLoads = new ArrayList();
				for(capableMachine curM:curStage.getCapableMachines()) {
					candidate_machine_ID.add(curM.getMachineId());
					tempLoads.add(Loads[curM.getMachineId()-1]+curM.getCostTime());
				}
				int minCost = GA.INF;
				int minMachine = -1;
				for(int j=0;j<tempLoads.size();j++) {
					if(minCost>tempLoads.get(j)) {
						minCost=tempLoads.get(j);
						minMachine = j+1;
					}
				}
				//更新负载
				Loads[candidate_machine_ID.get(minMachine-1)-1]=minCost;
				//minMachine 就是当前基因位上的值，现在要找到当前基因的下标，更新MS
				MS[stage_count]=minMachine;
				stage_count++;
				
			}
		}
		
		return I;
	}
	/*
	public static individual GlobalInit(GA G) {
		
		order O = G.getOrder();
		individual I = new individual();
		int[] MS = new int[O.getOperationCount()];
		int[] OS = new int[O.getOperationCount()];
		chromosome C = new chromosome().setMachineSelection(MS).setStageSequence(OS);
		I.setChromosome(C);
		int[] Loads = new int[O.getN()];
		for(int load:Loads) load=0;//初始化负载为0
		int ptr_MS=0;//当前基因位
		for(Job tempJ:O.getJobs()) {
			for(stage tempS:tempJ.getStages()) {
				int[] temp_Loads = Loads.clone();
				int machineSize = tempS.getCapableMachines().size();
				for(int i=0;i<machineSize;i++) {
					capableMachine tempM = tempS.getCapableMachines().get(i);
					int mID=tempM.getMachineId();
					temp_Loads[mID-1]+=tempM.getCostTime();
				}
				//找到最小加工时间的机器
				int minIndex = 0;
				int minCost = temp_Loads[0];
				for(int i=1;i<O.getN();i++) {
					if(temp_Loads[i]<temp_Loads[minIndex]) {
						minCost=temp_Loads[i];
						minIndex=i;
					}
				}
				int selectMachineID=minIndex+1;
				int machineOrder = -1;
				for(int i=1;i<=machineSize;i++) {
					if(selectMachineID==tempS.getCapableMachines().get(i).getMachineId()) {
						machineOrder = i;
						break;
					}
				}
				//该工序选择了这个机器，需要把对应位置的基因位更新
				MS[ptr_MS]=machineOrder;
				ptr_MS++;
			}
		}
		
		return I;
		
	}
	*/
	public static void main(String[] args) {
		main3();
	}
	public static void main3() {
		int popSize=20;
		int epochs=100;
		double mutationRate=0.001;
		double crossoevrRate=0.6;
		double PG=0.6;
		double PL=0.3;
		double PR=0.1;
		String file_path="MK02.fjs";
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path);
		
		individual I = LocalInit(ga);
		I.decode(ga);
		String output=I.getGant().toString();
		System.out.println(output);
	}
	public static void main2() {
		int popSize=20;
		int epochs=100;
		double mutationRate=0.001;
		double crossoevrRate=0.6;
		double PG=0;
		double PL=0;
		double PR=1;
		String file_path="MK01.txt";
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path);
		try {
					individual I1 = randomInit(ga);
					I1.decode(ga);
					mutation(ga,I1);			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	public static void main1() {
		int [] a = new int[] {1,3,2};
		int [][] b = new int[6][3];
		arrange(b,a,0,a.length-1);
		for(int[] p : b) {
			for(int q:p) {
//				System.out.print(q);
			}
//			System.out.println();
		}
	}
	//用于生成全排列
	private static int row=0;
	static void arrange(int b[][],int a[], int start, int end) {
		
		if (start == end) {		
			for(int i=0;i<a.length;i++) {
				b[row][i]=a[i];
			}
			row++;
			return;
		}
		for (int i = start; i <= end; i++) {
			swap(a, i, start);
			arrange(b,a, start + 1, end);
			swap(a, i, start);
		}
	}
 
	static void swap(int arr[], int i, int j) {
		int te = arr[i];
		arr[i] = arr[j];
		arr[j] = te;
	}
}
class pop{
	private int popSize;
	private List<individual> individuals;
	private individual bestIndividual;
	private individual worstIndividual;
	private double popDiversity;
	public void setpopDiversity(double popDiversity) {
		this.popDiversity = popDiversity;
	}
	public double getpopDiversity() {
		Set<individual> S = new HashSet();
		for(individual I:this.getIndividuals())
			S.add(I);
		
		this.popDiversity=(double) S.size()/this.getIndividuals().size();
		
		return popDiversity;
//		return S.size();
	}
	public double getAverageCost() {
		double sum=0;
		for(individual I:this.getIndividuals())
			sum+=I.getTotalCost();
		return sum/this.popSize;
	}
	public int getPopSize() {
		return popSize;
	}
	public pop setPopSize(int popSize) {
		this.popSize = popSize;
		return this;
	}
	public List<individual> getIndividuals() {
		return individuals;
	}
	public pop setIndividuals(List<individual> individuals) {
		this.individuals = individuals;
		return this;
	}
	public individual getBestIndividual() {
		individual bestI= this.getIndividuals().get(0);
		for(int i=1;i<this.popSize;i++) {
			individual tempI = this.getIndividuals().get(i);
			if(tempI.getTotalCost()<bestI.getTotalCost())
				bestI=tempI;
		}
		this.bestIndividual = bestI;
		return bestIndividual;
	}
	public void setBestIndividual(individual bestIndividual) {
		this.bestIndividual = bestIndividual;
	}
	public individual getWorstIndividual() {
		individual worstI= this.getIndividuals().get(0);
		for(int i=1;i<this.popSize;i++) {
			individual tempI = this.getIndividuals().get(i);
			if(tempI.getTotalCost()>worstI.getTotalCost())
				worstI=tempI;
		}
		this.worstIndividual = worstI;
		return worstIndividual;
	}
	public void setWorstIndividual(individual worstIndividual) {
		this.worstIndividual = worstIndividual;
	}
}