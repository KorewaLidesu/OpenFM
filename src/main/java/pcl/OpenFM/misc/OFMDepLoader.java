package pcl.OpenFM.misc;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.minecraftforge.fml.relauncher.IFMLCallHook;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class OFMDepLoader implements IFMLLoadingPlugin {
	public void load(Object fileObject) throws IOException, URISyntaxException {
		System.out.println("Starting OpenFM DepLoader!");
		File f = new File("mods" + File.separator);
		f.mkdirs();
		final String path = "assets/openfm/deps";
		if(fileObject instanceof File coremodFile && coremodFile.isFile()) {  // Run with JAR file
			JarFile jar = new JarFile(coremodFile);
			final Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
			while(entries.hasMoreElements()) {
				final String name = entries.nextElement().getName();
				if (name.startsWith(path + "/") && name.endsWith(".jar")) { //filter according to the path
					InputStream jarStream = getClass().getClassLoader().getResourceAsStream(name);
					Path p = Paths.get(name);
					String file = p.getFileName().toString();
					System.out.println("Extracting file: " + file);
					try (FileOutputStream fos = new FileOutputStream(f + File.separator + file);){
						byte[] buf = new byte[2048];
						int r;
						while(-1 != (r = jarStream.read(buf))) {
							fos.write(buf, 0, r);
						}
					}
				}
			}
			jar.close();
		} else {
			System.out.println("ERROR: Can't detect valid JAR");
		}
	}

	@Override
	public String[] getASMTransformerClass() {
		return null;
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return getClass().getName();
	}

	@Override
	public void injectData(Map<String, Object> data) {
		try {
			load(data.get("coremodLocation"));
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}
}