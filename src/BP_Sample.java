
public class BP_Sample {
	private String id;
	private int location;//第一个，前半，后半，最后
	private int chromosome_index;
	public int getChromosome_index() {
		return chromosome_index;
	}
	public void setChromosome_index(int chromosome_index) {
		this.chromosome_index = chromosome_index;
	}
	private int remain_time;//短，中，长
	private int cost;//大，中，小
	private int machine_load_heavy;//heavy，light
	private int machine_load_light;
	public int getMachine_load_heavy() {
		return machine_load_heavy;
	}
	public void setMachine_load_heavy(int machine_load_heavy) {
		this.machine_load_heavy = machine_load_heavy;
	}
	public int getMachine_load_light() {
		return machine_load_light;
	}
	public void setMachine_load_light(int machine_load_light) {
		this.machine_load_light = machine_load_light;
	}
	private int priority;//根据染色体判断1，2，3，4，5，6
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getLocation() {
		return location;
	}
	public void setLocation(int location) {
		this.location = location;
	}
	public int getRemain_time() {
		return remain_time;
	}
	public void setRemain_time(int remain_time) {
		this.remain_time = remain_time;
	}
	public int getCost() {
		return cost;
	}
	public void setCost(int cost) {
		this.cost = cost;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
