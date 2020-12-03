public class Shell extends Thread {
    //Variable to keep track of Shell[x]%
    private int x;

    //Constructor for Shell that sets x
    public Shell()
    {
        x = 1;
    }

    public void run()
    {
        //Print prompt
        SysLib.cout("Shell" + "[" + x + "]%");

        //Read from buffer
        StringBuffer buffer = new StringBuffer();
        SysLib.cin(buffer);

        //Parse and split the line into strings in array
        String[] array = SysLib.stringToArgs(buffer.toString());

        //Exit if command found
        if (array[0].equals("exit"))
        {
            SysLib.exit();
        }

        //Use variable to keep track of start of current subArray
        int beginningPtr = 0;

        //Loop through array and check for & concurrent and ; sequential
        for (int i = 0; i < array.length; i++)
        {
            //If & concurrent, create and exec subArray of the previous command
            if (array[i].equals("&"))
            {
                int size = i - beginningPtr;
                String[] subArray = new String[size];
                for (int j = 0; j < subArray.length; j++)
                {
                    subArray[j] = array[beginningPtr];
                    beginningPtr++;
                }
                beginningPtr = i + 1;
                int tid = SysLib.exec(subArray);
                SysLib.cout("Started Thread tid=" + tid + "\n");
                SysLib.exit();
            }
            //If & sequential, create and exec subArray of the previous command
            //Use while loop to keep calling thread
            else if (array[i].equals(";"))  //; sequential
            {
                int size = i - beginningPtr;
                String[] subArray = new String[size];
                for (int j = 0; j < subArray.length; j++)
                {
                    subArray[j] = array[beginningPtr];
                    beginningPtr++;
                }
                beginningPtr = i + 1;
                int tid = SysLib.exec(subArray);
                SysLib.cout("Started Thread tid=" + tid + "\n");
                int rtid = 0;
                while (tid != rtid)
                {
                    rtid = SysLib.join();
                }
                SysLib.exit();
            }
            //Command
            else
            {
                //Add these commands once & or ; reached
            }
        }
        //Increment counter
        x++;
    }
}