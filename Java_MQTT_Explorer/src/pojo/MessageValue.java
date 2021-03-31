package pojo;

/*
 * @author Florian Steinkellner
 * @date March 26, 2021
 */
public class MessageValue {
	String name;
	String value;
	String unit;
	String addition;
	double valueDouble;
	
	public MessageValue(String name, String value, String unit, String addition) {
		this.name = name;
		this.value = value;
		convertValueToDouble();
		this.unit = unit;
		this.addition = addition;
	}
	
	@Override
	public String toString() {
		return String.format("Value %s [%s %s] - %s", getName(), getValue(), getUnit(), getAddition());
	}
	
	public void convertValueToDouble() {
		this.valueDouble = StringOp.toDouble(value);
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

	public double getValueDouble() {
		if (this.valueDouble == 0 || this.valueDouble == Double.MIN_VALUE) {
			convertValueToDouble();
		}
		
		return valueDouble;
	}

	public void setValueDouble(double valueDouble) {
		this.valueDouble = valueDouble;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
