package in.control;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
public class ControlScreen {
	private Robot control=null;
	public ControlScreen()
	{	try{
			control=new Robot();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
/*	public boolean pressKeysInSequence(int keyEvent)
	{
		try{
			control.keyPress(keyEvent);
			control.keyRelease(keyEvent);
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public boolean pressKeysInSequence(int firstKeyEvent, int secondKeyEvent)
	{
		try{
			control.keyPress(firstKeyEvent);
			control.keyPress(secondKeyEvent);
			control.keyRelease(secondKeyEvent);
			control.keyRelease(firstKeyEvent);
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public boolean pressKeysInOrder(int firstKeyEvent, int secondKeyEvent, int thirdKeyEvent)
	{	//for combinations like Ctrl+Tab+Tab
		try{
			control.keyPress(firstKeyEvent);
			control.keyPress(secondKeyEvent);
			control.keyRelease(secondKeyEvent);
			control.keyPress(thirdKeyEvent);
			control.keyRelease(thirdKeyEvent);			
			control.keyRelease(firstKeyEvent);
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public boolean pressKeysInSequence(int firstKeyEvent, int secondKeyEvent, int thirdKeyEvent, int fourthKeyEvent)
	{
		//for sequences like Ctrl+Alt+Del+End
		try{
			control.keyPress(firstKeyEvent);
			control.keyPress(secondKeyEvent);
			control.keyPress(thirdKeyEvent);
			control.keyPress(fourthKeyEvent);
			control.keyRelease(fourthKeyEvent);						
			control.keyRelease(thirdKeyEvent);			
			control.keyRelease(secondKeyEvent);
			control.keyRelease(firstKeyEvent);
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	public boolean pressKeysInSequence(int firstKeyEvent, int secondKeyEvent, int thirdKeyEvent)
	{
		//for sequences like Ctrl+Alt+Del+End
		try{
			control.keyPress(firstKeyEvent);
			control.keyPress(secondKeyEvent);
			control.keyPress(thirdKeyEvent);
			control.keyRelease(thirdKeyEvent);			
			control.keyRelease(secondKeyEvent);
			control.keyRelease(firstKeyEvent);
			return true;
		}catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
*/	
	public Robot getControl() {
		return control;
	}
	public void setControl(Robot control) {
		this.control = control;
	}
	public boolean execute(Command command)
	{
		System.out.println("Robot: I am going to execute command");
		char[] sequence=command.getSequence();
		String[] instructions=command.getInstructions();
		int order=0;
		Class clazz=KeyEvent.class;
		Field field;
		try{
				for(int i=0;i<sequence.length;i++)
				{
					order=((char)(sequence[i])-(char)('a'));
					String f=instructions[order/2];
					field=clazz.getField(f);
					if(order%2==0)
					{
						//System.out.println("Printing"+field.get(null));
						control.keyPress((int)field.get(null));//press "KeyEvent."+instructions[(order/2)]
					}else
					{
						//System.out.println("Printing"+field.get(null));
						control.keyRelease((int)field.get(null));//press "KeyEvent."+instructions[(order/2)]
					}
				}
				System.out.println("Robot: I have executed command");
				return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
//	public static void main(String args[])
//	{	
//		ControlScreen trial=new ControlScreen();
//		trial.execute(new Command(new String[] {"VK_ALT","VK_TAB"},new char[] {'a','c','d','b'}));
////		Class clazz=KeyEvent.class;
////		Field field;
////		String f="VK_A";
////		try{
////			field=clazz.getField(f);
////			System.out.println((int)field.get(null));
////		}catch(Exception e)
////		{	e.printStackTrace();}
////		//trial.pressKeysInSequence(KeyEvent.VK_A);
//		//trial.pressKeysInSequence(KeyEvent.VK_ALT, KeyEvent.VK_TAB);
//		//trial.pressKeysInOrder(KeyEvent.VK_ALT, KeyEvent.VK_TAB, KeyEvent.VK_TAB);
//		//trial.pressKeysInSequence(KeyEvent.VK_CONTROL, KeyEvent.VK_PAGE_UP);
//		//trial.pressKeysInSequence(KeyEvent.VK_CONTROL, KeyEvent.VK_ALT, KeyEvent.VK_DELETE);
//	}
}
