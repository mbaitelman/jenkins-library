/**
 * Wait until a URL returns a http code inside a list of valid codes or a timeout reached (default: 10min).
 * <p>
 * Example:
 * <pre>
 * waitForHttpResponse url: 'https://www.example.com/path', codes: [200,201,203], timeout: [time: 20, unit: 'SECONDS']
 * </pre>
 * </p>
 * @param args the map of arguments
 */
def call(Map args) {
    String url = args.url
    int id = args.id
    def codes = args.codes ?: [200]
    def timeoutArgs = args.timeout ?: [time: 15, unit: 'MINUTES']
    timeout(timeoutArgs) {
        waitUntil(initialRecurrencePeriod: 10000, quiet: true) {
            return codes.contains( httpRequest ( url: url, validResponseCodes: '100:599', wrapAsMultipart: false, quiet: true).getStatus() )
        }
    }
}
