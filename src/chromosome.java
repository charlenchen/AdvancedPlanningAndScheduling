import java.util.ArrayList;
import java.util.List;

public class chromosome {
	private int[] machineSelection;//机器选择序列
	private int[] stageSequence;//工序顺序序列
	public int[] getMachineSelection() {
		return machineSelection;
	}
	public chromosome setMachineSelection(int[] machineSelection) {
		this.machineSelection = machineSelection;
		return this;
	}
	public int[] getStageSequence() {
		return stageSequence;
	}
	public chromosome setStageSequence(int[] stageSequence) {
		this.stageSequence = stageSequence;
		return this;
	}
	chromosome(){
		
	}
	
}
class decode{
	private ArrayList<ArrayList<Integer>> Jm;//机器顺序矩阵
	public ArrayList<ArrayList<Integer>> getJm() {
		return Jm;
	}
	public void setJm(ArrayList<ArrayList<Integer>> jm) {
		Jm = jm;
	}
	public ArrayList<ArrayList<Integer>> getT() {
		return T;
	}
	public void setT(ArrayList<ArrayList<Integer>> t2) {
		T = t2;
	}
	private ArrayList<ArrayList<Integer>> T;//时间顺序矩阵
}
