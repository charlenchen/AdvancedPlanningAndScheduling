import java.util.ArrayList;
import java.util.List;

public class chromosome {
	private int[] machineSelection;//����ѡ������
	private int[] stageSequence;//����˳������
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
	private ArrayList<ArrayList<Integer>> Jm;//����˳�����
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
	private ArrayList<ArrayList<Integer>> T;//ʱ��˳�����
}
