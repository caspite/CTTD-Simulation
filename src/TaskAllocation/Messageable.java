package TaskAllocation;

import java.util.List;

public interface Messageable {

	public void recieveMessage(List<Message> msgs);
	public void createMessage(Messageable reciver, double context);

}
