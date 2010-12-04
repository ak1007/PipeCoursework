A good way to start looking at and working with the Pipe source is
using the open source IDE Eclipse. Eclipse can be downloaded from

http://www.eclipse.org

You will need to get the latest 3.0 milestone build - the 2.1 release
will not work with SourceForge's CVS server.

The following instructions will get you up and running with Pipe
using CVS.

1) Download, install and run Eclipse. Click on 'Go to the workbench'

2) Select 'Window > Open Perspective > CVS Repository Exploring'

3) In the blank frame on the left titled 'CVS Repositories', right
   click and do 'New > Repository Location...'

4) In the dialog that appears, enter the following:
   Host:            pipe2.cvs.sourceforge.net
   Repository path: /cvsroot/pipe2

   If you are a registered developer with commit rights, enter your
   sourceforge username & password - otherwise enter:

   User:       Anonymous
   Password:   <leave blank>

   For connection type, select 'pserver' for anonymous CVS or 'extssh'
   otherwise.

   Leave port as default, and leave validate connection enabled, then
   click 'Finish'.

5) The connection should then be validated. If everything goes ok
   there should now be a new entry in the CVS Repositories pane.
   Expand this pane using the arrow on the left.
   Then expand the 'HEAD' entry. You should now see an entry 'PIPE'.

6) Right click on the 'PIPE' entry and choose 'Check Out As...'.
   In the dialog that appears select 'Check out as a project
   configured using the New Project Wizard', and click in Finish.

7) Select 'Java Project' from the Wizards list and click Next.

8) Type 'pipe' as the project name and click Finish (not Next).
   At this point Eclipse will probably ask if you want to switch
   to the Java perspective, so click Yes.

9) You now have to wait while Pipe is checked out from CVS (this may
   take a little while). Once it is finished you'll see a lot of
   errors listed in the bottom pane.

10) Click on the pipe project in the Package Explorer (in the left
    pane) and select 'Project > Properties' from the main Eclipse
    menu.

11) In the list on the left, select 'Java Build Path'. Then select the
    Source tab. Click 'Add Folder...' and check the 'Resources',
    'src' and 'test' directories. 

12) Also on the source tab:
          - change the Default output folder to "pipe/build/app"
          - check the "Allow output folders for source folders" box
          - expand the pipe/test entry in the list of source folders,
          click on the "Output folder:" for this source folder and click
          the Edit... button. Select the "Specific output folder" option 
          and enter "build/test" in the box. Click OK.
    
13) On the Libraries tab, click Add JARs...Select junit.jar and abbot.jar
    from the pipe/test/lib directory and click OK. Click OK.
    
14) Go to Window->Preferences, then Ant->Runtime and click on the Global Entries
    line in the Classpath tab. Now click Add Jars.. and add junit.jar, xalan.jar
    and serializer.jar from the pipe/test/lib directory and click OK. Click OK.

15) You may see a warning about removing generated resources. Click Yes.

16) You should now be back in the Java perspective, and once the
    workspace has been rebuilt, the errors shown previously will have
    dissapeared. Finally we need to set up a run environment.

17) Select 'Run > Run...' from the main Eclipse menu. In the dialog
    that appears, select 'Java Application' from the list on the left,
    and then click New.

18) Change the Name 'New_configuration' to 'Pipe'. Click on the
    Search... button in the 'Main Class' section. Select RunGui in the
    dialog that appears. Then click Apply, followed by Run.

Pipe should now appear! From now on you just need to click on the
running man icon in the toolbar to run Pipe within Eclipse.

Hopefully the above is enough to get you started, read the Eclipse
documentation to learn how to edit and compile (it's all very simple
you'll find).

CVS commits within Eclipse are a little more complicated, again read
the documentation for more info.