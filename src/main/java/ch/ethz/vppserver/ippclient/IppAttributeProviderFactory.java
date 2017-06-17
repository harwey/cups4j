package ch.ethz.vppserver.ippclient;

public class IppAttributeProviderFactory {
  public static IIppAttributeProvider createIppAttributeProvider() {
    return IppAttributeProvider.getInstance();
  }
}
