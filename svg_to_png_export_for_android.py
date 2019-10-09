# This script is used to create PNGs of multiple resolutions from
# an SVG.
# It requires inkscape to be installed

import os
import subprocess
import sys

if len(sys.argv) != 3:
   print(f"Usage: python3 {os.path.basename(__file__)} <input_file.svg> <output_file.png>")
   sys.exit()

input_filename = sys.argv[1]
output_filename = sys.argv[2]
output_filename_no_extension = os.path.splitext(output_filename)[0]

# standard sizes
#sizes = [("mdpi", 24),("hdpi", 36),("xhdpi", 48),("xxhdpi", 72), ("xxxhdpi", 96)]

sizes = [("mdpi", 86),("hdpi", 128),("xhdpi", 170),("xxhdpi", 256), ("xxxhdpi", 340)]

new_root_dir = f"{output_filename_no_extension}_PNGs"
os.mkdir(new_root_dir)

for size in sizes:
    new_child_dir = os.path.join(new_root_dir, size[0])
    os.mkdir(new_child_dir)
    subprocess.check_call(["inkscape", "--export-png", os.path.join(new_child_dir, output_filename), input_filename, "-w", str(size[1])])

