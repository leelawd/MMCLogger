package net.moddedminecraft.mmclogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.bukkit.scheduler.BukkitRunnable;

public class CreateFile extends BukkitRunnable
{
	public File file;

	CreateFile(File f)
	{
		this.file = f;
	}

	public void run() {
		BufferedWriter buffwriter = null;
		FileWriter filewriter = null;
		try {
			this.file.createNewFile();
			filewriter = new FileWriter(this.file, true);
			buffwriter = new BufferedWriter(filewriter);
			buffwriter.flush(); return;
		}
		catch (IOException e) {}finally {
			try {
				if (buffwriter != null) {
					buffwriter.close();
				}
				if (filewriter != null) {
					filewriter.close();
				}
			}
			catch (IOException e) {}
		}
	}
}
