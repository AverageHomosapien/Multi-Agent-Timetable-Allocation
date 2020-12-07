# Mult-Agent Allocation of Tutorials to Students
This was a 4th year coursework done as part of the Multi-Agent Systems module.

## Overview
We were given the task of allocating tutorial classes to students using the Jade Java framework. Students have preferences for the tutorials they wish to attend,
and would attempt to switch to these slots.

My approach used a TimetableAgent, which held a messageboard of tutorials. It would activate StudentAgents one-by-one by offering slots, facilitate the swapping 
and then checking if the StudentAgents were happy with the slots they'd been given.

I fully detail my approach in the '40205163.pdf' file.
