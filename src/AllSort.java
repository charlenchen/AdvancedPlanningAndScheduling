
 
public class AllSort {
	private static int row=0;
	public static void permutation(int[] buf, int start, int end,int[][] b,int row) {
		if (start == end) {// ��ֻҪ���������һ����ĸ����ȫ����ʱ��ֻҪ�Ͱ��������������
			for (int i = 0; i <= end; i++) {
				b[row][i]=buf[i];
			}
			row++;
		} else {// �����ĸȫ����
			for (int i = start; i <= end; i++) {
				int temp = buf[start];// ���������һ��Ԫ���������Ԫ��
				buf[start] = buf[i];
				buf[i] = temp;
 
				permutation(buf, start + 1, end,b,row);// ����Ԫ�صݹ�ȫ����
 
				temp = buf[start];// ������������黹ԭ
				buf[start] = buf[i];
				buf[i] = temp;
			}
		}
	}
	public static void main(String[] args) {
		int[] a = new int[]{1,15,4,16};
		int[][] b = new int[24][4];
		permutation(a,0,3,b,0);
		for(int [] r:b) {
			for(int e:r) {
				System.out.print(" "+e);
			}
			System.out.println();
		}
	}
}