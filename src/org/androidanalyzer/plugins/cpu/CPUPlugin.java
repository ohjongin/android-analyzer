package org.androidanalyzer.plugins.cpu;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.androidanalyzer.Constants;
import org.androidanalyzer.core.Data;
import org.androidanalyzer.core.utils.Logger;
import org.androidanalyzer.plugins.AbstractPlugin;

/**
 * CPUPlugin class used for gathering CPU data
 * 
 */
public class CPUPlugin extends AbstractPlugin {

	private static final String NAME = "CPU Plugin";
	private static final String PLUGIN_VERSION = "1.0.0";
	private static final String PLUGIN_VENDOR = "ProSyst Software GmbH";
	private static final String PARENT_NODE_NAME = "CPU";
	private static final String TAG = "Analyzer-CPUPlugin";
	private static final String OS_ARCH = "os.arch";
	private static final String INSTRUCTION_SET = "Instruction Set";
	private static final String MANUFACTURER = "Manufacturer";
	private static final String CPU_NAME = "Name";
	private static final String OPER_FREQ = "Operational Frequency";

	private static final String CPU_FREQ_FILE = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
	private static final String CPU_INFO_FILE = "/proc/cpuinfo";

	private static final String ARCH_ARM = "arm";
	private static final String FREQ_METRIC = "MHz";

	private static final String DESCRIPTION = "Collects information on the device\'s main CPU";
	private String status = Constants.METADATA_PLUGIN_STATUS_PASSED;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.cpu.PluginCommunicator# getPluginName()
	 */
	@Override
	public String getPluginName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator# getPluginVersion()
	 */
	@Override
	public String getPluginVersion() {
		return PLUGIN_VERSION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginVendor()
	 */
	@Override
	public String getPluginVendor() {
		// TODO Auto-generated method stub
		return PLUGIN_VENDOR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator#getTimeout ()
	 */
	@Override
	public long getPluginTimeout() {
		return 10000;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginDescription()
	 */
	@Override
	public String getPluginDescription() {
		return DESCRIPTION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#isPluginRequiredUI()
	 */
	@Override
	public boolean isPluginRequiredUI() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.cpu.PluginCommunicator# getData()
	 */
	@Override
	protected Data getData() {
		Logger.DEBUG(TAG, "getData in CPU Plugin");
		ArrayList<Data> children = new ArrayList<Data>(4);
		Data parent = new Data();
		try {
			parent.setName(PARENT_NODE_NAME);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set parent node!", e);
			status = "Could not set CPU parent node!";
		}

		try {
			/* Manifacturer */
			Data manifacturer = new Data();
			manifacturer.setName(MANUFACTURER);
			String manifac = getCpuManufacturer();
			Logger.DEBUG(TAG, "Manifacturer: " + manifac);
			if (manifac != null && manifac.length() > 0) {
				manifacturer.setValue(manifac);
				manifacturer.setStatus(Constants.NODE_STATUS_OK);
			} else {
				manifacturer.setStatus(Constants.NODE_STATUS_FAILED);
				manifacturer.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
			}
			children.add(manifacturer);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Manifacturer node!", e);
			status = "Could not set Manifacturer node!";
		}

		String os_arch = System.getProperty(OS_ARCH);

		/* Instruction Set */
		try {
			Data insSet = new Data();
			insSet.setName(INSTRUCTION_SET);
			Logger.DEBUG(TAG, "OS Architecture: " + os_arch);
			if (os_arch != null && os_arch.length() > 0) {
				insSet.setValue(os_arch);
				insSet.setStatus(Constants.NODE_STATUS_OK);
			} else {
				insSet.setStatus(Constants.NODE_STATUS_FAILED);
				insSet.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
			}
			children.add(insSet);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set Instruction Set", e);
			status = "Could not set Instruction Set";
		}

		/* CPU Name */
		try {
			Data name = new Data();
			name.setName(CPU_NAME);
			String nameString = getCpuName();
			Logger.DEBUG(TAG, "CPU name: " + nameString);
			if (nameString != null && nameString.length() > 0) {
				name.setValue(nameString);
				name.setStatus(Constants.NODE_STATUS_OK);
			} else {
				name.setStatus(Constants.NODE_STATUS_FAILED);
				name.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
			}
			children.add(name);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set CPU Name", e);
			status = "Could not set CPU Name";
		}

		/* CPU frequency */
		try {

			Data frequency = new Data();
			frequency.setName(OPER_FREQ);
			String freq = getCpuFrequency();
			boolean validValue = false;
			try {
				Double test = Double.parseDouble(freq);
				validValue = true;
			} catch (Exception e) {
			}
			Logger.DEBUG(TAG, "Frequency: " + freq);
			if (freq != null && freq.length() > 0 && validValue) {
				frequency.setValue(freq);
				frequency.setStatus(Constants.NODE_STATUS_OK);
				frequency.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
				frequency.setValueMetric(FREQ_METRIC);
			} else {
				frequency.setStatus(Constants.NODE_STATUS_FAILED);
				frequency.setValue(Constants.NODE_STATUS_FAILED_UNAVAILABLE_VALUE);
			}
			frequency.setValueType(Constants.NODE_VALUE_TYPE_DOUBLE);
			children.add(frequency);
		} catch (Exception e) {
			Logger.ERROR(TAG, "Could not set CPU Frequency!", e);
			status = "Could not set CPU Frequency!";
		}

		addToParent(parent, children);
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.AbstractPlugin#getPluginStatus()
	 */
	@Override
	protected String getPluginStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.cpu.PluginCommunicatorAbstract
	 * #getPluginClassName()
	 */
	@Override
	protected String getPluginClassName() {
		return this.getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.androidanalyzer.plugins.PluginCommunicator# stopDataCollection()
	 */
	@Override
	protected void stopDataCollection() {
		this.stopSelf();
	}

	/**
	 * @return
	 */
	private String getCpuName() {
		String arch = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(CPU_INFO_FILE));
			String str;
			while ((str = in.readLine()) != null) {
				if (str.startsWith("Processor")) {
					arch = str;
					break;
				}
			}
			in.close();
		} catch (IOException e) {
			Logger.ERROR(TAG, "Could not open " + CPU_INFO_FILE, e);
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}

		if (arch != null && arch.length() > 0) {
			StringTokenizer token = new StringTokenizer(arch, ":");
			while (token.hasMoreElements()) {
				arch = (String) token.nextElement();
			}
			arch = arch.trim();
		}

		return arch;
	}

	private String getCpuFrequency() {
		StringBuffer buff = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(CPU_FREQ_FILE));
			String str;
			while ((str = in.readLine()) != null) {
				buff.append(str);
			}
			in.close();
		} catch (IOException e) {
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
		Logger.DEBUG(TAG, "frequency size : " + buff.length());
		try {
			Double freq = new Double(buff.toString());
			freq = freq / 1000;
			Logger.DEBUG(TAG, "frequency : " + freq + FREQ_METRIC);
			return freq.toString();
		} catch (Exception e) {
			return null;
		}
	}

	private String getCpuManufacturer() {
		String manifac = null;
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(CPU_INFO_FILE));
			String str;
			while ((str = in.readLine()) != null) {
				if (str.startsWith("CPU implementer")) {
					manifac = str;
					break;
				}
			}
			in.close();
		} catch (IOException e) {
			Logger.ERROR(TAG, "Could not open " + CPU_INFO_FILE, e);
			return null;
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}

		String arch = getCpuName();
		String architecture = System.getProperty(OS_ARCH);
		if ((architecture != null && architecture.length() > 0 && architecture.startsWith(ARCH_ARM))
				|| (arch != null && arch.length() > 0 && arch.startsWith(ARCH_ARM.toUpperCase()))) {
			if (manifac != null && manifac.length() > 0) {
				StringTokenizer token = new StringTokenizer(manifac, ": \tx");
				while (token.hasMoreElements()) {
					manifac = (String) token.nextElement();
				}
			}

			switch (Integer.parseInt(manifac, 16)) {
			case 0x41:
				manifac = "ARM Limited";
				break;
			case 0x44:
				manifac = "Digital Equipment Corporation";
				break;
			case 0x4D:
				manifac = "Motorola, Freescale Semiconductor Inc.";
				break;
			case 0x51:
				manifac = "QUALCOMM Inc.";
				break;
			case 0x56:
				manifac = "Marvell Semiconductor Inc.";
				break;
			case 0x69:
				manifac = "Intel Corporation";
				break;

			default:
				manifac = null;
				break;
			}
			Logger.DEBUG(TAG, "CPUPlugin Manifacturer : " + manifac);
			return manifac;
		}
		return null;
	}
}
