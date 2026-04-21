> [!WARNING]
> This README is provided for informational purposes only and does not create any support, maintenance, service level,
> or other service commitment by Neterium.
>
> Please read carefully the [legal notice](#Legal-Notice) section below before using this repository.

# Neterium SDK Samples

This repository contains a collection of sample Spring applications that are built using
[Neterium SDK](https://github.com/neterium/integration-framework-sdk) components.

Each application is designed to be simple and focused, making it an easy and practical way to get familiar with
the SDK’s core concepts. By exploring these examples, you can better understand how the SDK’s building blocks work
together in real-world scenarios.
The samples highlight common patterns, recommended practices, and typical use cases you’re likely to encounter when
building your own solutions.
They are also a great starting point for experimentation and hands-on learning.
Finally, these applications can serve as inspiration or a foundation for building a Proof of Concept, accelerating your
path from idea to implementation.

## Content

The following sample applications are available for demo purposes, sorted by **increasing complexity**:

| Functional scope | Application                                                                       | JetFlow ? | JetScan ? |
|------------------|-----------------------------------------------------------------------------------|-----------|-----------|
| First steps      | [Minimal application](sdk-demo-minimal/README.md)                                 | X         | X         |
| Private Lists    | [PrivateLists application](sdk-demo-private-lists/README.md)                      | X         | X         |
| Mapping          | [Mapping application](sdk-demo-mapping/README.md)                                 | X         |           |
| Throttling       | [Throttling **standalone** application](sdk-demo-throttling/standalone/README.md) | X         | X         |
| Throttling       | [Throttling **web** application](sdk-demo-throttling/web/README.md)               | X         | X         |
| Screening        | [**Name** screening application](sdk-demo-name-screening/README.md)               |           | X         |
| Alert management | **Transaction** screening application (*)                                         | X         |           |

(*) This more elaborated sample application is available in a **separate** GitHub repository.
Please contact us to request access to it.

## Requirements

- Java 21 or above
- Maven 3.9.10 or above
- Public internet access (no VPN required)
- **Credentials** for Neterium API - [contact](https://www.neterium.io/contact-us) us to get some.

## Prerequisites

The SDK libraries should be installed in your (local or shared) repository. Refer to the
SDK [documentation](https://github.com/neterium/neterium-sdk) for guidance on how to build it.

## Build

Ensure to build the whole project (with all Maven modules) using the root [pom](pom.xml) :

```shell
mvn clean install
```

The build output should then look like:

```text
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary for Neterium Client SDK :: Demo 1.0.0-SNAPSHOT:
[INFO] 
[INFO] Neterium Client SDK :: Demo ........................ SUCCESS [  0.122 s]
[INFO] Neterium Client SDK :: Demo :: Shared Module ....... SUCCESS [  0.833 s]
[INFO] Neterium Client SDK :: Demo :: Minimal App ......... SUCCESS [  0.651 s]
[INFO] Neterium Client SDK :: Demo :: Mapping App ......... SUCCESS [  0.356 s]
[INFO] Neterium Client SDK :: Demo :: Private Lists ....... SUCCESS [  0.293 s]
[INFO] Neterium Client SDK :: Demo :: Throttling App ...... SUCCESS [  0.410 s]
[INFO] Neterium Client SDK :: Demo :: Throttling WebApp ... SUCCESS [  0.525 s]
[INFO] Neterium Client SDK :: Demo :: Name Screening App .. SUCCESS [  0.532 s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  0.856 s
[INFO] Finished at: 2026-01-27T11:38:12+01:00
[INFO] ------------------------------------------------------------------------
```

## Contributing

See [CONTRIBUTING](CONTRIBUTING.md)

## Security

See [SECURITY](SECURITY.md)

# Legal Notice

## Purpose

This repository is intended to facilitate and accelerate integration with the Neterium screening API by providing
reusable SDK components, wrappers, and reference integration examples. It is published to reduce implementation friction
and to help developers build their own applications and workflows around the Neterium API.

This repository is intended for:

- Neterium customers,
- Neterium integration partners and other solution providers integrating the Neterium API, and
- Neterium internal teams supporting product, engineering, compliance, and go-to-market activities.

### What’s in this repository

This repository contains the following materials (collectively, the “Open Source Components”):

- SDKs, wrappers and helper modules enabling interaction with the Neterium API;
- Integration samples and reference implementations illustrating common integration patterns;
- A minimal demonstrator user interface (UI) provided for illustration purposes only; and
- Documentation and examples, including installation guidance and usage instructions.

### What this repository is not

For the avoidance of doubt, this repository and the Open Source Components are not:

- an end-to-end product;
- a full-featured user interface; or
- a managed service (including hosting, operations, monitoring, support, or incident handling).

End-to-end implementations (including alert/case management, storage, workflows and operationalisation) are typically
delivered by Neterium’s integration partners and/or implemented by users within their own environments.

## License

Licensed under the Apache License, Version 2.0 - see the [LICENSE](LICENSE) file.

## Open Source Policy

See [POLICY](POLICY.md)

## Disclaimer

### Examples only / no product confusion

The Open Source Components are provided as generic components and integration examples only. They are not intended to
constitute a complete product or a production-ready solution.

### No support

Neterium does not provide helpdesk support, incident handling, troubleshooting, consulting, or implementation assistance
for the Open Source Components.

### No SLA / no maintenance commitment

Neterium makes no commitment to maintain, update, patch, correct, or otherwise improve the Open Source Components and
provides no service level agreement (SLA), response times, availability commitments, or remediation timelines. Neterium
may modify, suspend, or discontinue this repository (in whole or in part) at any time.

### AS IS / no warranty

The Open Source Components are provided on an “AS IS” basis, without warranties or conditions of any kind, whether
express or implied, to the extent permitted by the applicable open-source license.

### Production use at user’s sole responsibility

Any use of the Open Source Components in production (including regulated or compliance-critical contexts) is undertaken
solely at the user’s own risk and responsibility. Users remain responsible for determining suitability, testing,
validation, security hardening, operational controls, and regulatory/compliance assessments.

### Partner positioning

This repository is intended to be complementary to Neterium’s integration partners and does not aim to provide an
end-to-end solution.

### Trademarks

“Neterium” and related names and logos are trademarks and/or service marks of Neterium SRL. This repository does not
grant any rights to use Neterium’s trademarks, except as necessary for reasonable and customary
use in describing the origin of the Open Source Components and reproducing attribution notices.

## Contact

- Product / repository inquiries: product@neterium.com
- Security reports: security@neterium.com
- Legal / licensing questions: legal@neterium.com

