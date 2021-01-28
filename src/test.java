import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class test {
	public static String file_path= "Mk01.txt";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a=0;
		int b=0;
		int c[] = new int[] {1,2,3};
		int d[] = new int[] {1,2,3};
		String ea="123";
		String eb="123";
		String ec = "";
		String ed = "";
		int popSize=100;
		int epochs=200;
		double mutationRate=0.01;
		double crossoevrRate=0.8;
		double PG=0.5;
		double PL=0.2;
		double PR=0.3;
		String file_path="MK01.txt";
		GA ga = new GA(popSize,epochs,mutationRate,crossoevrRate,PG,PL,PR,file_path);
		individual I1 = GAoperations.randomInit(ga);
		I1.decode(ga);
		int[] MS=I1.getChromosome().getMachineSelection();
		int[] OS=I1.getChromosome().getStageSequence();
		Set<individual> S = new HashSet();
		individual I2 = new individual().setChromosome(new chromosome().setMachineSelection(MS.clone()).setStageSequence(OS.clone()));
		I1.decode(ga);
		I2.decode(ga);
		S.add(I1);
		S.add(I2);
		S.add(I2);
		System.out.println(I1.hashCode()==I2.hashCode());
		System.out.println(I1.equals(I2));
		System.out.println(S.size());
		System.out.print(S);
		System.out.println(I1==I2);
		System.out.println(I1.hashCode());
		System.out.println(I2.hashCode());
		List<individual> l = new ArrayList();
		l.add(I1);
		l.add(I2);
		pop Pop = new pop().setIndividuals(l);
		System.out.println(Pop.getpopDiversity());
	
	}
	static boolean dictSeq(int[] array) {
	       // �����Ҷ˿�ʼ��һ�����ұ�С��λ��
	       int j = -1;
	       for (int i=array.length-2; i>=0; i--) {
	           if (array[i] < array[i+1]) {
	               j = i;
	               break;
	           }
	       }
	       // ��ʱ�Ѿ����������
	       if (j == -1) {	
	           System.out.println("end");
	           return false;
	       }
	       // j��߱�jλ�ô����С��һ��λ��
	       int k = -1;
	       int min = Integer.MAX_VALUE;
	       for (int i=j; i<array.length; i++) {
	           if (array[i] > array[j] && array[i] <= min) { // ����Ҫ�ҵ����һ����������ڴ�����ͬԪ�صļ��ϻ���ִ����磺0122
	               min = array[i];
	               k = i;
	           }
	       }
	       // ����j��k��ֵ
	       int tmp = array[j];
	       array[j] = array[k];
	       array[k] = tmp;
	       // ��j��ߵ����н��з�ת
	       int left = j+1;
	       int right = array.length - 1;
	       while (left < right) {
	           int t = array[left];
	           array[left] = array[right];
	           array[right] = t;
	           left ++;
	           right --;
	       }
	       for (int i : array) {
	           System.out.print(i + ", ");
	       }
	       System.out.println();
	       return true;
	   }
	private static int row=0;
	static void arrange(int b[][],int a[], int start, int end) {
		
		if (start == end) {		
			for(int i=0;i<a.length;i++) {
				b[row][i]=a[i];
			}
			for (int i : a) {
				System.out.print(i);
			}
			System.out.println();
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

	public static void testJob() {
		Job job = new Job("J1");
		Map<String,Map<String,Integer>> order = new HashMap();
		Map<String,Integer> mpa = new HashMap();
		mpa.put("Machine1", 20);
		mpa.put("Machine2", 30);
		Map<String,Integer> mpa2 = new HashMap();
		mpa2.put("Machine3", 20);
		mpa2.put("Machine2", 100);
		order.put("order1", mpa);
		order.put("order2",mpa2);
		
		System.out.print(job.toString());
		System.out.println(mpa.toString());
	}
	public static void testTable() {
		List table = new ArrayList();
		int m=-1,n=-1,l=-1;
		Scanner input = new Scanner(System.in);
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			 
			/* ����TXT�ļ� */
			String pathname = file_path; // ����·�������·�������ԣ������Ǿ���·����д���ļ�ʱ��ʾ���·��
			File filename = new File(pathname); // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(
					new FileInputStream(filename)); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";
			
			int count=0;
			while (true) {
				count++;
				line = br.readLine(); // һ�ζ���һ������
				if(line==null || "".equals(line)) break;
				
					Job temp = new Job("Job"+count);
					String[] tokens = line.trim().split(" ");
					System.out.println("count:"+count);
					int num = Integer.parseInt(tokens[0].trim());
					for(String token:tokens) {
						System.out.print(token+"-");
					}
					System.out.println();
				
			}
			
 
		} catch (Exception e) {
			e.printStackTrace();

				
		}
		System.out.println("m"+m+"n"+n);
	}
}
