package service;

import channel.Connector;
import channel.colibri.ColibriChannel;
import channel.obix.CoapChannel;
import channel.obix.ObixChannel;
import exception.ConfigurationException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used to operate on properties files.
 */
public class Configurator {

    private ResourceBundle bundle;
    private static Configurator instance;

    protected Configurator() {
        this.bundle = ResourceBundle.getBundle("config");
    }

    public static Configurator getInstance() {
        if(instance == null) {
            instance = new Configurator();
        }
        return instance;
    }

    /**
     * This method returns the obix lobbies URIs which are specified in the .properties file of the given bundle.
     *
     * @return The parsed obix lobby URIs.
     * @throws ConfigurationException Is thrown, if there is no oBIX Lobby provided in the parsed .properties file.
     */
    private List<ObixChannel> getObixCoapChannels() throws ConfigurationException {
        List<ObixChannel> oBIXchannels = new ArrayList<ObixChannel>();
        int i = 1;
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.contains("oBIXLobby")) {
                String uri = bundle.getString(key);
                uri = uri.replaceAll("\\s+", "");
                String baseUri = uri.split("/")[0];
                ObixChannel channel;
                if (uri.contains(",")) {
                    Integer port = Integer.parseInt(uri.split(",")[1]);
                    channel = new CoapChannel(baseUri, uri, port, getObservedTypes());
                } else {
                    channel = new CoapChannel(baseUri, uri, getObservedTypes());
                }
                oBIXchannels.add(channel);
            }
        }

        if (oBIXchannels.size() == 0) {
            throw new ConfigurationException("No oBIXLobby URI in config file!");
        }

        return oBIXchannels;
    }

    /**
     * This method returns a list of Strings which represent the types which will be observed from oBIX.
     *
     * @return The list of observed types.
     * @throws ConfigurationException Is thrown, if there are no types provided in the parsed .properties file.
     */
    private List<String> getObservedTypes() throws ConfigurationException {
        List<String> observedTypes = new ArrayList<String>();
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.contains("observedTypes")) {
                String[] types = bundle.getString(key).replaceAll("\\s+", "").split(",");
                Collections.addAll(observedTypes, types);
            }
        }

        if (observedTypes.size() == 0) {
            throw new ConfigurationException("No observed types in config file!");
        }

        return observedTypes;
    }

    /**
     * This method returns the URI of the Colibri channel in the .properties file of the given bundle.
     *
     * @return The parsed colibri channel URI.
     * @throws ConfigurationException Is thrown, if there is no colibri channel URI provided in the parsed .properties file.
     */
    private ColibriChannel getColibriChannel() throws ConfigurationException {
        if (bundle.containsKey("colibriChannel")){
            String[] channelParts = bundle.getString("colibriChannel").replaceAll("\\s+", "").split(",");
            return new ColibriChannel("obixConnector", channelParts[0], Integer.parseInt(channelParts[1]));
        }else{
            throw new ConfigurationException("No colibri channel URI in config file!");
        }
    }

    /**
     * This method returns the obix lobbies URIs which are specified in the .properties file of the given bundle.
     *
     * @return The parsed obix lobby URIs.
     * @throws ConfigurationException Is thrown, if there is no oBIX Lobby provided in the parsed .properties file.
     */
    public List<Connector> getConnectors() throws ConfigurationException {
        List<Connector> connectors = new ArrayList<>();
        ColibriChannel colibriChannel = getColibriChannel();
        connectors.addAll(getObixCoapChannels().stream().map(obixChannel -> new Connector(obixChannel, colibriChannel,
                getConnectorAddress(), getConnectorIPAddress())).collect(Collectors.toList()));

        if (connectors.size() == 0) {
            throw new ConfigurationException("Cannot parse connectors of config file!");
        }
        return connectors;
    }

    /**
     * This method returns the address of the oBIX Connector in the .properties file of the given bundle.
     *
     * @return The parsed address of the obix Connector.
     * @throws ConfigurationException Is thrown, if there is no obix Connector address provided in the parsed .properties file.
     */
    public String getConnectorAddress() throws ConfigurationException {
        if (bundle.containsKey("connectorAddress")){
            return bundle.getString("connectorAddress");
        }else{
            throw new ConfigurationException("No obix Connector address URI in config file!");
        }
    }

    /**
     * This method returns the IP address of the oBIX Connector in the .properties file of the given bundle.
     *
     * @return The parsed IP address of the obix Connector.
     * @throws ConfigurationException Is thrown, if there is no obix Connector IP address provided in the parsed .properties file.
     */
    public String getConnectorIPAddress() throws ConfigurationException {
        if (bundle.containsKey("connectorIPAddress")){
            return bundle.getString("connectorIPAddress");
        }else{
            throw new ConfigurationException("No obix Connector IP address URI in config file!");
        }
    }

    /**
     * This method returns the available parameter types which are specified in the .properties file of the given bundle.
     *
     * @return The parsed parameter types.
     * @throws ConfigurationException Is thrown, if there are no parameters provided in the parsed .properties file.
     */
    public List<String> getAvailableParameterTypes() throws ConfigurationException {
        List<String> parameterTypes = new ArrayList<String>();
        Enumeration<String> keys = bundle.getKeys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            if (key.contains("parameterTypes")) {
                String[] types = bundle.getString(key).replaceAll("\\s+", "").split(",");
                Collections.addAll(parameterTypes, types);
            }
        }

        if (parameterTypes.size() == 0) {
            throw new ConfigurationException("No parameter types in config file!");
        }

        return parameterTypes;
    }

    /**
     * This method returns the time to wait for a Status Response in milliseconds which is specified in the .properties file of the given bundle.
     *
     * @return time to wait for a status response
     * @throws ConfigurationException Is thrown, if the property is not provided in the parsed .properties file.
     */
    public int getTimeWaitingForStatusResponseInMilliseconds() throws ConfigurationException {
        if (bundle.containsKey("timeWaitingForStatusResponseInMilliseconds")){
            return Integer.parseInt(bundle.getString("timeWaitingForStatusResponseInMilliseconds"));
        }else{
            throw new ConfigurationException("No time to wait for a status response in config file!");
        }
    }

    /**
     * This method returns the times to resend a message which is specified in the .properties file of the given bundle.
     *
     * @return how often the message will be resent
     * @throws ConfigurationException Is thrown, if the property is not provided in the parsed .properties file.
     */
    public int getTimesToResendMessage() throws ConfigurationException {
        if (bundle.containsKey("resendMessageTimes")){
            return Integer.parseInt(bundle.getString("resendMessageTimes"));
        }else{
            throw new ConfigurationException("No times to resend a message configured in the config file!");
        }
    }

    /**
     * This method returns String representation of a newline specified in the .properties file of the given bundle.
     *
     * @return the String representation of a newline
     * @throws ConfigurationException Is thrown, if the property is not provided in the parsed .properties file.
     */
    public String getNewlineString() throws ConfigurationException {
        if (bundle.containsKey("newline")){
            return bundle.getString("newline");
        }else{
            throw new ConfigurationException("No times to resend a message configured in the config file!");
        }
    }
}
