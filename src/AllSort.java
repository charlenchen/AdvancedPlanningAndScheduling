
 
public class AllSort {
	private static int row=0;
	public static void permutation(int[] buf, int start, int end,int[][] b,int row) {
		if (start == end) {// 当只要求对数组中一个字母进行全排列时，只要就按该数组输出即可
			for (int i = 0; i <= end; i++) {
				b[row][i]=buf[i];
			}
			row++;
		} else {// 多个字母全排列
			for (int i = start; i <= end; i++) {
				int temp = buf[start];// 交换数组第一个元素与后续的元素
				buf[start] = buf[i];
				buf[i] = temp;
 
				permutation(buf, start + 1, end,b,row);// 后续元素递归全排列
 
				temp = buf[start];// 将交换后的数组还原
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