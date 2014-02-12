package ool.com.ofpm.client;

/*
 * 装置管理DB よりデータを取得して提供するインターフェースです。
 */
public interface DeviceManagementDBClient {
	/*
	 * TODO 未検討です
	 * OFPマネージャがOFP-Switchを識別するための、IPアドレスを取得します。
	 * 識別にdatapath_idを利用するならば不要です。
	 */
	public String getIPFromDeviceName(String deviceName);

	/*
	 * OFPマネージャがスイッチの状態を登録する場合に利用します。
	 * スイッチのIPアドレスからDeviceNameを取り出します。
	 * 識別にdatapath_idを利用するならば不要です。
	 */
	public String getDeviceNameFromIP(String ip);

	/*
	 * OFPマネージャがポートの状態を登録する場合に利用します。
	 * 装置管理DBのdevice_nameとport_idをキーにしてport_nameを取得します。
	 * Orient DB側の_idとRyuから取得できるスイッチの番号が一致し、
	 * _idにて更新できる場合は、不要です。
	 */
	public String getPortNames(String deviceName, int portNumber);

	/*
	 * 装置管理DBよりSwitchやPort、Node、NICの情報を取得します。
	 */
	public SwitchData getSwitchInfo(String deviceName);
	public PortData getPortInfo(String deviceName, int portNumber);
	public NodeData getNodeInfo(String deviceName);
	public NICData getNodeInfo(String deviceName);
}
