import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class GA {
	public static final int INF=65535;
	private int popSize;
	public pop getCurPop() {
		return curPop;
	}

	public void setCurPop(pop curPop) {
		this.curPop = curPop;
	}
	private pop curPop;
	private int epochs;
	private double mutationRate;
	private double crossoevrRate;
	private double PG;
	private double PL;
	private double PR;
	private order order;
	GA(int popSize,int epochs,double mutationRate,double crossoevrRate,double PG, double PL,double PR,String file_path){
		this.popSize=popSize;
		this.epochs=epochs;
		this.mutationRate=mutationRate;
		this.crossoevrRate=crossoevrRate;
		this.PG=PG;
		this.PL=PL;
		this.PR=PR;
		order o = new order();
		o.setJobs(o.read_file(file_path));
		o.setCapableHeavy();
		this.order = o;
	}
	
	public int getPopSize() {
		return popSize;
	}

	public void setPopSize(int popSize) {
		this.popSize = popSize;
	}

	public int getEpochs() {
		return epochs;
	}

	public void setEpochs(int epochs) {
		this.epochs = epochs;
	}

	public double getMutationRate() {
		return mutationRate;
	}

	public void setMutationRate(double mutationRate) {
		this.mutationRate = mutationRate;
	}

	public double getCrossoevrRate() {
		return crossoevrRate;
	}

	public void setCrossoevrRate(double crossoevrRate) {
		this.crossoevrRate = crossoevrRate;
	}

	public double getPG() {
		return PG;
	}

	public void setPG(double pG) {
		PG = pG;
	}

	public double getPL() {
		return PL;
	}

	public void setPL(double pL) {
		PL = pL;
	}

	public double getPR() {
		return PR;
	}

	public void setPR(double pR) {
		PR = pR;
	}

	public order getOrder() {
		return order;
	}

	public void setOrder(order order) {
		this.order = order;
	}

	public void run() {
		
		pop Pop = new pop().setPopSize(this.popSize);
		this.setCurPop(Pop);
		//初始化种群
		GAoperations.init(this);
		int epoch = 0;
		while(epoch<epochs) {//在这里设置终止条件
			if(epoch==150) {
				System.out.println("hello");
			}
			epoch++;
			//选择
			GAoperations.select(this);
			//交叉
			GAoperations.crossOver(this);
			//变异
			GAoperations.mutation(this);
			int bestcost= this.getCurPop().getBestIndividual().getTotalCost();
			int worstcost= this.getCurPop().getWorstIndividual().getTotalCost();
			System.out.printf("epoch:%03d  bestcost:%2d  worstcost:%2d  avgCost:%3.2f  diversity:%.2f%%\n",epoch,bestcost,worstcost ,this.getCurPop().getAverageCost(),100*this.curPop.getpopDiversity());
//			System.out.println("epoch:"+epoch+"  cost:"+cost);
		}
		//结束后输出最后一代的情况
		
		System.out.println("finalWorstCost:"+this.curPop.getWorstIndividual().getTotalCost());
		System.out.println("finalBestCost:"+this.curPop.getBestIndividual().getTotalCost());
	}
	public static int main2() {
		int popSize=1000;
		int epochs=100;
		double mutationRate=0.01;
		double crossoevrRate=0.8;
		double PG=1;
		double PL=0;
		double PR=0;
		String file_path="MK01.txt";
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path);
		ga.run();
		return ga.getCurPop().getBestIndividual().getTotalCost();
	}
	public static void main(String[] args) {
		//generate_network_input2();
		//	main1();
		generate_network_input();
		//test_network();
	}
	public static void generate_network_input2() {
		Scanner input = new Scanner(System.in);
		int popSize=1000;
		int epochs=100;
		double mutationRate=0.01;
		double crossoevrRate=0.8;
		double PG=0.5;
		double PL=0.2;
		double PR=0.3;
		System.out.println("input instance");
//		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Barnes/Text/";
		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Brandimarte_Data/Text/";
		//String postfix = "Mk02.fjs";
		String postfix = input.nextLine();
		String file_path_input=prefix+postfix+".fjs";
//		String file_path_input = "Mk02.fjs";
		String output_prefix = "C:\\Users\\MAIBENBEN\\Desktop\\柔性车间调度\\Untitled Folder\\network_input\\";
		
		String file_path_output="network1_input_"+postfix+".csv";
		file_path_output= output_prefix + file_path_output;
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path_input);
		individual I0 = GAoperations.GlobalInit(ga);
		I0.decode(ga);
		I0.toSample2(ga, file_path_output);
		System.out.println("input network2 OS chromosome");
		/*
		String line = input.nextLine();
		int OS[] = new int[ga.getOrder().getOperationCount()];
		String[] tokens = line.trim().split(",");
		for(int i=0;i<tokens.length;i++) {
			OS[i]=Integer.parseInt(tokens[i].trim());
		}
		int[] MS_Pri=new int[55];
		*/
		int[] OS = new int[] {10 ,14 ,1 ,7 ,10 ,11 ,9 ,14 ,2 ,11 ,15 ,12 ,7 ,9 ,4 ,2 ,8 ,13 ,15 ,6 ,14 ,1 ,3 ,11 ,10 ,6 ,5 ,9 ,6 ,6 ,9 ,8 ,5 ,9 ,4 ,6 ,13 ,8 ,5 ,3 ,12 ,11 ,3 ,6 ,4 ,15 ,13 ,10 ,12 ,1 ,8 ,3 ,10 ,14 ,1 ,9 ,10 ,4 ,3 ,6 ,15 ,13 ,8 ,1 ,11 ,12 ,5 ,2 ,7 ,14 ,10 ,5 ,4 ,3 ,15 ,13 ,8 ,6 ,9 ,7 ,14 ,7 ,8 ,4 ,13 ,3 ,15 ,11 ,12 ,2 ,9 ,6 ,9 ,5 ,10 ,4 ,3 ,8 ,13 ,1 ,15 ,11 ,12 ,2 ,14 ,10};
		int[] MS_Pri = new int[] {0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,0 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1 ,1};
		for(int i=0;i<100;i++) {
			individual I1 = GAoperations.network_scheduling_with_MSPriority(ga, OS, MS_Pri);
			I1.decode(ga);
			ga.write_file(I1.getSequence().toFile(), "test.csv");
			int [] T = I1.getChromosome().getStageSequence();
			
			System.out.print("\n"+I1.toString());
		}
		
	}
	public static void generate_network_input() {
		Scanner input = new Scanner(System.in);
		int popSize=1000;
		int epochs=100;
		double mutationRate=0.01;
		double crossoevrRate=0.8;
		double PG=0.5;
		double PL=0.2;
		double PR=0.3;
		System.out.println("input instance");
//		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Barnes/Text/";
		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Brandimarte_Data/Text/";
		//String postfix = "Mk02.fjs";
		String postfix = input.nextLine();
		String file_path_input=prefix+postfix+".fjs";
//		String file_path_input = "Mk02.fjs";
		String output_prefix = "C:\\Users\\MAIBENBEN\\Desktop\\柔性车间调度\\Untitled Folder\\network_input\\";
		
		String file_path_output="network1_input_"+postfix+".csv";
		file_path_output= output_prefix + file_path_output;
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path_input);
		individual I0 = GAoperations.GlobalInit(ga);
		I0.decode(ga);
		I0.toSample2(ga, file_path_output);
		System.out.println("input network2 OS chromosome");
		String line = input.nextLine();
		int OS[] = new int[ga.getOrder().getOperationCount()];
		String[] tokens = line.trim().split(",");
		for(int i=0;i<tokens.length;i++) {
			OS[i]=Integer.parseInt(tokens[i].trim());
		}
		for(int i=0;i<100;i++) {
			individual I1 = GAoperations.network_scheduling(ga, OS);
			I1.decode(ga);
			ga.write_file(I1.getSequence().toFile(), "test.csv");
			int [] T = I1.getChromosome().getStageSequence();
			
			System.out.print("\n"+I1.toString());
		}
		
	}
	public static void test_network() {
		int popSize=1000;
		int epochs=100;
		double mutationRate=0.01;
		double crossoevrRate=0.8;
		double PG=0.5;
		double PL=0.2;
		double PR=0.3;
//		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Barnes/Text/";
		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Brandimarte_Data/Text/";
		String postfix = "Mk02.fjs";
		String file_path_input=prefix+postfix;
//		String file_path_input = "Mk02.fjs";
		String file_path_output="output.csv";
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path_input);
		Scanner input = new Scanner(System.in);
		
		int OS[]=new int[] { 8,  2,  4,  5,  3,  9, 10,  7,  4,  1,  8,  1,  6,  1,  2,  3,  6,
		        5,  9,  6,  3,  7, 10,  5,  8,  9,  2,  6,  4,  7,  8, 10,  4,  5,
		        10,  1,  4,  8,  6,  2,  3, 10,  7,  9,  1,  2,  3,  5,  1,  9,  7,
		         5,  3,  2, 10,  8,  6,  4};
		
		individual I = GAoperations.network_scheduling(ga, OS);
		I.decode(ga);
		ga.write_file(I.getSequence().toFile(), "test.csv");
		int [] T = I.getChromosome().getStageSequence();
		for(int t:T) {
			System.out.print(t+"\t");
		}
		System.out.print("\n"+I.toString());
	}
	public static void main1() {
		int popSize=1000;
		int epochs=100;
		double mutationRate=0.01;
		double crossoevrRate=0.8;
		double PG=0.5;
		double PL=0.2;
		double PR=0.3;
//		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Barnes/Text/";
		String prefix = "TextData/Monaldo/Fjsp/Job_Data/Brandimarte_Data/Text/";
		String postfix = "Mk03.fjs";
		String file_path_input=prefix+postfix;
//		String file_path_input = "Mk02.fjs";
		String file_path_output="output.csv";
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path_input);
		ga.run();
		ga.getCurPop().getIndividuals().sort(Comparator.naturalOrder());
		String output1 = ga.getCurPop().getIndividuals().get(0).getGant().toString();
		System.out.println(output1);
		String output2 = ga.getCurPop().getIndividuals().get(0).getSequence().toString();
		System.out.println(output2);
		pop p=ga.getCurPop();
		ga.write_file(ga.getCurPop().getBestIndividual().getSequence().toFile(), "test.csv");
		ga.getCurPop().getBestIndividual().toSampleWithMachineSelection(ga,"MS_Sample.csv",false);
		ga.getCurPop().getBestIndividual().toNeuralNetwork2(ga);
//		write_data(ga, file_path_output,postfix);
	}
	public static void write_file(String outputs,String file_path) {
		try (BufferedWriter out = new BufferedWriter(new FileWriter(new File(file_path)))) {
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
	public static void write_data(GA G,String file_path,String data_source) {
		try {
			File f= new File("D:\\成长\\大三上半年\\eclipse-workspace-2020-2021-autumn-term\\FJSP2"+ File.separator + file_path) ; // 声明File对象
			// 第2步、通过子类实例化父类对象
			Writer out = null ; // 准备好一个输出的对象
			out = new FileWriter(f,true) ; // 通过对象多态性，进行实例化
			// 第3步、进行写操作
			gant machine_ = G.getCurPop().getBestIndividual().getGant();
			final int Gap = 10;
			final int N = G.getOrder().getN();
			final int  TotalCost= G.getCurPop().getBestIndividual().getTotalCost();
			int Len=-1,End=-1;
			if(TotalCost%Gap==0) {
				Len=TotalCost/Gap;
				End = 0;
			}else {
				Len=TotalCost/Gap+1;
				End=TotalCost%Gap;
			}
				
			int counter[][] = new int[N][TotalCost+1];
			double result[] = new double[Len];
			for(int[] i:counter)
				for(int j:i )
					j=0;
			int ptr1 =0;
			for(machine m:machine_.getMachines()) {
				int ptr2=0;
				for(node n:m.getNodes()) {
					int start = n.getStartTime();
					int end = n.getFinishTime();
					for(int i=start;i<=end;i++) 
						counter[ptr1][i]=1;
				}
				ptr1++;
			}	
			String output=data_source+",";
			for(int i=0;i<Len-1;i++) {
				int col_start=i*Gap+1;
				int sum=0;
				for(int row=0;row<N;row++) {
					//N行，Gap列
					for(int col=0;col<Gap;col++) {
						sum+=counter[row][col+col_start];
					}
				}
				result[i]=1.0*sum/Gap;
			}
			if(End==0) {
				int sum=0;
				for(int row=0;row<N;row++) {
					//N行，Gap列
					for(int col=0;col<Gap;col++) {
						sum+=counter[row][col+Len-Gap];
					}
					
				}
				result[Len-1]=1.0*sum/Gap;
			}else {
				int sum=0;
				for(int row=0;row<N;row++) {
					//N行，Gap列
					for(int col=0;col<End;col++) {
						sum+=counter[row][col+Len-End];
					}
					
				}
				result[Len-1]=1.0*sum/End;
			}
			for(int i=0;i<Len-1;i++) {
				output+=result[i]+",";
			}
			output+=(result[Len-1])+"\n";
			out.write(output) ;// 将内容输出，保存文件
			// 第4步、关闭输出流
			out.close() ;// 关闭输出流
		}
		catch(Exception e){
			e.printStackTrace();		
		}
	}
}
