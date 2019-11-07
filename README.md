# COMP329 Robotics and Autonomous Systems Assignment 1

### Ashley Rushworth, Jack Roach and James Daniels

This assessment covers the design of a robot that is able to navigate an arena, and build a map of the arena.

**This assignment counts for 50% of the final grade for the module. The deadline for the assignment  is Friday 25th October (i.e. end of week 5).  You will also be expected to give a demo of your app, and answer questions during an allocated slot for 25 minutes during week 7.**

**The syllabus provides an indication of how much time a typical student is expected to spend on the assignment.  Please try to plan your activities to stay in line with this.  Whilst it is understood that you may require further time to complete part of your work, or that you may choose to spend more time to achieve a certain functionality (for example if you choose to challenge yourself using the sensor model), it is not expected that you should take significantly longer than this time.**

This is the detailed specification of the assignment:

### Main Aims ###

- You are required to write a robot program that will enable your robot to explore the arena and produce a map, using **Behaviours** through the Subsumption Package.
- Your robot should explore its arena and produce a map using **occupancy grid techniques**, indicating the probability of each cell being occupied, unoccupied or whether the probability is unknown.  In your lectures, two approaches were discussed; either could be used.
  - _Note that a small number of **additional marks** may be obtained by using the Sensor Model (approach 2), but this may take additional time, so plan your time accordingly._
- The map should be **displayed on the robot's LCD screen** throughout the search (such that one can view the progress of the mapping process). 
  - When the robot finishes the demo, the map should be displayed until a final button (e.g. ESCAPE) is pressed, so that the map can be inspected. 
  - The map should be viewable either using text, drawn graphically, or both (for example, pressing a button toggles between the two). 
  - Any text based representation should display the probability to at least 1 significant digit. 
  - Note that any final occupancy grid could include cells representing the boundary of the arena. However, students can assume that the boundary is known.
  - _**Additional marks** can be obtained by displaying a map on the screen of a PC, to indicate progress, probabilities and strategies._
- Each robot will be allowed at least 5 minutes to map the arena, but no more than 10 minutes.
  - Marks will be awarded on **coverage of the map**, and the **strategy used to explore the arena**, as well as enduring the **best accuracy** in the given time.
  - The robot should indicate (using an audio and/or visual cue such as changing the colour of the buttons) to indicate when the map is finished.

### Environment ###

- Each arena will be configured to be 7x6 cells in size, where each cell is 25x25cm.  These cells will be marked out on each of the bays (or arenas) in the Robotics Teaching Lab (Lab 4).
- The arena will have a small number (between 2 and 4) of obstacles, which will be placed at random locations during the demo. These obstacles will be 25x25x10cm in dimension (i.e. the heavy obstacles used in the Robot Lab). They are intended to be easily detectable, but should not move when collisions occur.  
- Each arena will have a number of randomly distributed colour cells, which you could use to assist the localisation.  In addition, a 20mm boundary is used to mark out each cell.  You may use this boundary and the coloured cells to assist with localisation, which may gain you extra marks, and improve your overall solution.  However, no additional marks are available for displaying the location of the coloured cells.

### Robot ###

- The robot is assumed to start from a known location, typically corresponding to the bottom left of a grid (i.e. at cell 0,0) in the centre of the grid (i.e. at approximately 12.5cm right, 12.5cm up), with an orientation of heading North.
  - _Groups may choose to start from a different location, but no additional marks will be available for this._
- Groups have the choice of configuring the sensors as they wish for the robot:
  - The front of the robot may be configured with:
    - **Two touch sensors** with bumpers, which can detect obstacles and overcome range limitations of the proximity sensors, or
    - **Two colour sensors** at the front, which can be used to assist with localisation
  - A range sensor is positioned on a medium motor that can rotate between -90 and +90 degrees from the direction of travel.  This can be configured with:
    - An **Ultrasound sensor**, that has a longer range (typically greater than 1m), and can better sense multiple cells, or
    - An **Infrared sensor**, that has a shorter range (typically no more than approximately 50cm).  However, it can also detect up to four beacons that can be placed in the corner of the bay (detection range is approximately 2m)
  - An additional sensor positioned in the centre of the robot.  This may be configured as:
    - A **colour sensor** that can detect the colour of the floor (including the boundaries and coloured cells)
    - A **gyroscope** that can provide rotational information to support localisation.

### Finally ###

- Be realistic about what you can achieve in the time allocated.  You may make slower progress if working towards the deadline when there is competition for the bays.
- Look carefully at the marking scheme when deciding on your approach.  Additional marks are available for excellent solutions, but these typically take additional work.  Remember that there is a law of diminishing returns, significantly more time may be required to obtain the additional marks.
- As a guide, if you need to work something out that I haven't discussed in the lectures, then please check with me or Josh as you might be trying to go beyond the remit of the assignment.
