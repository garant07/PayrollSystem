package maintenance.bin.src.DBConn;

import java.io.File;
import java.io.IOException;

public class FileManipulator 
{
	
	//constructor
	public FileManipulator(File file)
	{
		if (file.exists())
		{
			System.out.println("File found");
			//if file exists, delete the file and create a new one
			if(file.delete())
			{
				//System.out.println("File succesfully deleted.");
				//System.out.println("Creating a new file");
					try
					{
						if(file.createNewFile())
						{
							//System.out.println("File succesfully created");
						}
						else
						{
							System.out.println("Error");
						}
					}
					catch(IOException e)
					{
						System.out.println(e.getMessage());
					}
			}
			else 
			{
				System.out.println("Error");
			}
		}
		else
		{
			//System.out.println("File does not exists.");
			//System.out.println("Creating a new file");
			
				try
				{
					if(file.createNewFile())
					{
						//System.out.println("File succesfully created");
					}
					else
					{
						System.out.println("Error");
					}
				}
				catch(IOException e)
				{
					System.out.println(e.getMessage());
				}
		}//else
	}//constructor
	
}//class
