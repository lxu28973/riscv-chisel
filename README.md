RISCV Chisel Project
=======================

### How to get started
The first thing you want to do is clone this repo into a directory of your own.  I'd recommend creating a chisel projects directory somewhere
```sh
mkdir ~/ChiselProjects
cd ~/ChiselProjects

git clone https://github.com/lxu28973/riscv-chisel.git ChiselProject
cd ChiselProject
```

### Generate verilog
You should now have a project based on Chisel3 that can be run.<br/>
So go for it, at the command line in the project root.
```sh
sbt 'test:runMain riscv5stages.TopMain'
```

You should see a whole bunch of output that ends with something like the following lines
```
[warn] Multiple main classes detected.  Run 'show discoveredMainClasses' to see the list
[info] running riscv5stages.TopMain 
[info] [0.003] Elaborating design...
[info] [1.276] Done elaborating.
Computed transform order in: 412.8 ms
Total FIRRTL Compile Time: 2243.0 ms
file loaded in 0.271195238 seconds, 840 symbols, 809 statements
[info] [0.002] SEED 1608361638323
test Top Success: 0 tests passed in 1015 cycles in 0.222829 seconds 4555.07 Hz
[info] [0.196] RAN 1010 CYCLES PASSED
[success] Total time: 6 s, completed Dec 19, 2020, 3:07:23 PM
```
If you see the above then...

### It worked!
You are ready to go. 

Some backends (verilator for example) produce VCD files by default, while other backends (firrtl and treadle) do not.
You can control the generation of VCD files with the `--generate-vcd-output` flag.

To run the simulation and generate a VCD output file regardless of the backend:
```bash
sbt 'test:runMain riscv5stages.TopMain --generate-vcd-output on'
```

To run the simulation and suppress the generation of a VCD output file:
```bash
sbt 'test:runMain riscv5stages.TopMain --generate-vcd-output off'
```

## Development/Bug Fixes
This is the release version of chisel-template. If you have bug fixes or
changes you would like to see incorporated in this repo, please checkout
the main branch and submit pull requests against it.
