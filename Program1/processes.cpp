//Receives one argument upon invocation
//Search how many processes whose name is given in argv[1] are running on system
//Like ps -A | grep argv[1] | wc -l;
//View all proccesses running on system | proccesses text line by line for argv[1] | count lines

#include <stdlib.h>  //exit
#include <stdio.h>   //perror
#include <unistd.h>  //fork, pipe
#include <sys/wait.h>   //wait
#include <iostream>
using namespace std;

int main (int argc, char *argv[])
{   
    /*--------------------------------------------------------------------------------
    Parent process spawns a child that spawns a child that spanws a grand-child that 
    spawns a great grand-child (process -> command -> stdin -> stdout):
        Parent -> wait for child -> no change (stdin) -> no change (stdout)
        Child -> wc -l -> redir from g-child stdout -> no change             
        G-child -> grep argv[1] -> redir from gg-child stdout -> redir to child's stdin
        Gg-child -> ps -A -> no change (stdin) -> redir g-child stdin
    --------------------------------------------------------------------------------*/
    //Fork for wc process
    pid_t pid_wc = fork();  
    if (pid_wc < 0)
    {
        cerr << "Error!" << endl;
    } 
    else if (pid_wc == 0)  //Child process
    {
        //Create child-grandchild pipe
        int c_gc[2];
        pipe(c_gc);

        //Fork for grep process
        pid_t pid_grep = fork();

        if (pid_grep == 0)  //Grandchild process
        {
            //Create grandchild-great grandchild pipe
            int gc_ggc[2];
            pipe(gc_ggc);

            //Fork for ps process
            pid_t pid_ps = fork();

            if (pid_ps == 0)  //Great grandchild process
            {
                //Close stdin/read side
                close(gc_ggc[0]);   //0 is read/stdin

                //Dup2 (specify I/O of pipe) (redir stdout to grandchild's stdin)
                dup2(gc_ggc[1], 1);  //1 is write/stdout

                //Execute ps -A
                execlp("/bin/ps", "ps", "-A", NULL);    
            }
            else  //Grandchild process
            {
                //Wait
                wait(NULL);

                //Close stdout (grandchild <-> great grandchild pipe)
                close(gc_ggc[1]);
                
                //Dup2 (grandchild <-> great grandchild pipe) (redir stdin from great grandchild's stdout)
                dup2(gc_ggc[0], 0);  //0 is read/stdin

                //Close stdin (child <-> grandchild pipe)
                close(c_gc[0]);

                //Dup2 (child <-> grandchild pipe) (redir stdout to child's stdin)
                dup2(c_gc[1], 1);  //1 is write/stdout

                //Exec grep argv[1]
                execlp("/bin/grep", "grep", argv[1], NULL);  
            }
        }
        else  //Child process
        {
            //Wait
            wait(NULL);

            //Close stdout/write side
            close(c_gc[1]);  //1 is write/stdout
            
            //Dup2 (redir stdin from grandchild's stdout)
            dup2(c_gc[0], 0);  //0 is read/stdin

            //Exec wc -l
            execlp("/usr/bin/wc", "wc", "-l", NULL); 
        }
    }
    else  //Parent process
    {
        //Wait
        wait(NULL);

        exit(EXIT_SUCCESS);
    }
}
