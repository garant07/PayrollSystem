package maintenance.bin.src.Functions;

import java.awt.Component;
import java.awt.Container;

import javax.swing.AbstractButton;
import javax.swing.JMenu;

public class removeButton {
	public static void remove(Component comp, int indent) {
        for(int i=indent; i>0; --i)
            //System.out.print(' ');
        indent += 4;
        //System.out.print(comp.getClass().getName());
        if (comp instanceof AbstractButton) {
            comp.getParent().remove(comp);
            //System.out.println(" [removed]");
        } else
            //System.out.println();
        if (comp instanceof Container) {
            Component[] children = ((Container)comp).getComponents();
            for(int i = 0; i<children.length; ++i)
                remove(children[i], indent);
        }
        if (comp instanceof JMenu) {
            Component[] children = ((JMenu)comp).getMenuComponents();
            for(int i = 0; i<children.length; ++i)
                remove(children[i], indent);
        }
    }
}
