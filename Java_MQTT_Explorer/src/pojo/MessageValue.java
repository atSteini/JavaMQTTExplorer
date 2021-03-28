package pojo;

/*
 * @author Florian Steinkellner
 * @date March 22, 2021
 */
public class MessageValue {
	String name;
	String value;
	String unit;
	String addition;
	int valueInt;
	
	public MessageValue(String name, String value, String unit, String addition) {
		this.name = name;
		this.value = value;
		this.valueInt = StringOp.toInt(value);
		this.unit = unit;
		this.addition = addition;
	}
	
	@Override
	public String toString() {
		return String.format("Value %s [%s %s] - %s", getName(), getValue(), getUnit(), getAddition());
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getAddition() {
		return addition;
	}

	public void setAddition(String addition) {
		this.addition = addition;
	}

	public int getValueInt() {
		return valueInt;
	}

	public void setValueInt(int valueInt) {
		this.valueInt = valueInt;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
