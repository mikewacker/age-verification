# Proof-of-Concept: Privacy-Conscious Age Verification

As age verification for social media becomes more popular with federal and state lawmakers, some tech policy experts
[have said][rstreet-dne], "The technology to verify your age without violating your privacy does not exist."
But are those experts right? Rather than tell you, I will show you with a demo:

## Demo

This demo will set up two social media sites and a third-party age verification service.
The user interface is not yet available; some text will narrate what happens as we communicate with these sites.

**Part I: The Basics**

First, let's verify an account:

```
- Crackle and Pop are social media sites.
- CheckMyAge is a third-party age verification service.
- John Smith (40) and Billy Smith (13, son of John) have accounts on CheckMyAge.

================================================================================

John Smith uses CheckMyAge to verify his account on Crackle, "publius":
- On Crackle, John Smith begins the process to verify "publius".
- (Behind the scenes, Crackle contacts CheckMyAge.)
    - Crackle does NOT share the account name, "publius", with CheckMyAge.
- Crackle redirects John Smith to CheckMyAge.
- John Smith confirms with CheckMyAge that he wants to verify an account on Crackle.
    - CheckMyAge does NOT know which account on Crackle is being verified.
- (Behind the scenes, CheckMyAge sends an age certificate to Crackle.)
    - CheckMyAge does NOT share John Smith's real identity with Crackle.
- CheckMyAge redirects John Smith back to Crackle.
- Crackle confirms that "publius" is verified!
- Crackle has stored the following data for "publius":
    - Pseudonym: wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI
    - Age: 18+
```

CheckMyAge only shares two pieces of information with Crackle:

- a (secure) pseudonym
- an age range

Next, let's verify Billy, who is John's underage son:

```
Billy Smith uses CheckMyAge to verify his account on Crackle, "publius-jr":
- On Crackle, Billy Smith begins the process to verify "publius-jr".
- (Behind the scenes, Crackle contacts CheckMyAge.)
    - Crackle does NOT share the account name, "publius-jr", with CheckMyAge.
- Crackle redirects Billy Smith to CheckMyAge.
- Billy Smith confirms with CheckMyAge that he wants to verify an account on Crackle.
    - CheckMyAge does NOT know which account on Crackle is being verified.
- (Behind the scenes, CheckMyAge sends an age certificate to Crackle.)
    - CheckMyAge does NOT share Billy Smith's real identity with Crackle.
- CheckMyAge redirects Billy Smith back to Crackle.
- Crackle confirms that "publius-jr" is verified!
- Crackle has stored the following data for "publius-jr":
    - Pseudonym: vT47RJUVsiagXQvHACvJKjliGLM97QcBrFRk9PfmAxE
    - Age: 13-17
    - Guardian Pseudonym: wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI
```

You will note that Billy also has a guardian pseudonym (`wqhgWlb9...`); it matches the pseudonym for `publius`.
Thus, Crackle knows that `publius` is the guardian of `publius-jr`&mdash;even
though it does not know the real-life identify of either user.

Let's also verify some accounts for John and Billy on a different site.
You can skim over this, as we're repeating the same steps:

```
John Smith uses CheckMyAge to verify his account on Pop, "JohnS":
- On Pop, John Smith begins the process to verify "JohnS".
- (Behind the scenes, Pop contacts CheckMyAge.)
    - Pop does NOT share the account name, "JohnS", with CheckMyAge.
- Pop redirects John Smith to CheckMyAge.
- John Smith confirms with CheckMyAge that he wants to verify an account on Pop.
    - CheckMyAge does NOT know which account on Pop is being verified.
- (Behind the scenes, CheckMyAge sends an age certificate to Pop.)
    - CheckMyAge does NOT share John Smith's real identity with Pop.
- CheckMyAge redirects John Smith back to Pop.
- Pop confirms that "JohnS" is verified!
- Pop has stored the following data for "JohnS":
    - Pseudonym: Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE
    - Age: 18+

Billy Smith uses CheckMyAge to verify his account on Pop, "BillyS":
- On Pop, Billy Smith begins the process to verify "BillyS".
- (Behind the scenes, Pop contacts CheckMyAge.)
    - Pop does NOT share the account name, "BillyS", with CheckMyAge.
- Pop redirects Billy Smith to CheckMyAge.
- Billy Smith confirms with CheckMyAge that he wants to verify an account on Pop.
    - CheckMyAge does NOT know which account on Pop is being verified.
- (Behind the scenes, CheckMyAge sends an age certificate to Pop.)
    - CheckMyAge does NOT share Billy Smith's real identity with Pop.
- CheckMyAge redirects Billy Smith back to Pop.
- Pop confirms that "BillyS" is verified!
- Pop has stored the following data for "BillyS":
    - Pseudonym: 5L3siUtZ5-HAKlxMGjwvEjFB49FoLJdSpytsuE6WeQU
    - Age: 13-17
    - Guardian Pseudonym: Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE
```

You may notice, however, that a different set of pseudonyms is used on Pop.

**Part II: One Person, One Account**

Let's say that a clever underage hacker, Bobby Tables, manages to gain access to John Smith's account on CheckMyAge:

```
Bobby Tables uses CheckMyAge to verify his account on Crackle, "drop-table":
- On Crackle, Bobby Tables begins the process to verify "drop-table".
- Crackle redirects Bobby Tables to CheckMyAge.
- Bobby Tables tries to use John Smith's account on CheckMyAge to verify his account.
- (Behind the scenes, CheckMyAge sends an age certificate to Crackle.)
- Crackle rejects the age certificate!
```

Why did Crackle reject the age certificate? Here's what happened behind the scenes:

- The pseudonym of the person that was verifying an account was `wqhgWlb9...`.
- However, that pseudonym was already used to verify another account: `publius`.

The principle here is **one person, one account**:
one person (i.e., one account on CheckMyAge) can only verify one account on each site.

While no system is 100% foolproof, we can greatly increase the effectiveness of this system
with the one person, one account principle.

**Part III: Data Breach!**

What happens if a data breach occurs? Let's answer that question by giving hackers all the data:

```
User data stored on Crackle:
{
  "publius" : {
    "pseudonym" : "wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI",
    "ageRange" : "18+",
    "guardianPseudonyms" : [ ]
  },
  "publius-jr" : {
    "pseudonym" : "vT47RJUVsiagXQvHACvJKjliGLM97QcBrFRk9PfmAxE",
    "ageRange" : "13-17",
    "guardianPseudonyms" : [ "wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI" ]
  }
}

User data stored on Pop:
{
  "JohnS" : {
    "pseudonym" : "Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE",
    "ageRange" : "18+",
    "guardianPseudonyms" : [ ]
  },
  "BillyS" : {
    "pseudonym" : "5L3siUtZ5-HAKlxMGjwvEjFB49FoLJdSpytsuE6WeQU",
    "ageRange" : "13-17",
    "guardianPseudonyms" : [ "Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE" ]
  }
}

User data stored on CheckMyAge:
{
  "John Smith" : {
    "pseudonym" : "uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4",
    "ageRange" : "40",
    "guardianPseudonyms" : [ ]
  },
  "Billy Smith" : {
    "pseudonym" : "KB0b9pDo8j7-1p90fFokbgHj8hzbbU7jCGGjfuMzLR4",
    "ageRange" : "13",
    "guardianPseudonyms" : [ "uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4" ]
  }
}
```

To use John Smith as an example, John has a different pseudonym for each account he verified,
and also for his account on CheckMyAge. (Some cryptographic magic is involved here.)
Thus, even if a data breach occurs, you can not figure out that `publius` is John Smith.

**Conclusion**

If one engineer could accomplish this, imagine what a team of engineers with even more expertise could accomplish.

Privacy-conscious age verification is possible. We don't need the "experts"
who write ten-part series on how it cannot be done; we need the experts who find ten ways to make it better.

## Verbose Demo (More Technical)

The verbose demo will show you the HTTP requests that were being made by the scene.

*(For demo purposes, a custom `Account-Id` header is used to authenticate the user.
Obviously, a real implementation would not do that, and would implement proper authentication.)*

```
John Smith uses CheckMyAge to verify his account on Crackle, "publius":
- On Crackle, John Smith begins the process to verify "publius".
- (Behind the scenes, Crackle contacts CheckMyAge.)
    - Crackle does NOT share the account name, "publius", with CheckMyAge.
[HTTP request]
POST http://localhost:8080/api/verification-request
[HTTP response]
200
{
  "id" : "J0uVz49O3c_I_ooCi6xeN4AqQXNR5Ada9-ZR2ePC-0E",
  "siteId" : "Crackle",
  "expiration" : 1703402087,
  "redirectUrl" : "http://localhost:8090/api/linked-verification-request?request-id=J0uVz49O3c_I_ooCi6xeN4AqQXNR5Ada9-ZR2ePC-0E"
}

- Crackle redirects John Smith to CheckMyAge.
[HTTP request]
POST http://localhost:8090/api/linked-verification-request?request-id=J0uVz49O3c_I_ooCi6xeN4AqQXNR5Ada9-ZR2ePC-0E
[HTTP response]
200

- John Smith confirms with CheckMyAge that he wants to verify an account on Crackle.
    - CheckMyAge does NOT know which account on Crackle is being verified.
- (Behind the scenes, CheckMyAge sends an age certificate to Crackle.)
    - CheckMyAge does NOT share John Smith's real identity with Crackle.
[HTTP request]
POST http://localhost:8090/api/age-certificate
[HTTP response]
200
"http://localhost:8080/api/verification-state"

- CheckMyAge redirects John Smith back to Crackle.
- Crackle confirms that "publius" is verified!
[HTTP request]
GET http://localhost:8080/api/verification-state
[HTTP response]
200
{
  "status" : "VERIFIED",
  "verifiedUser" : {
    "pseudonym" : "wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI",
    "ageRange" : "18+",
    "guardianPseudonyms" : [ ]
  },
  "expiration" : 1705993787
}

- Crackle has stored the following data for "publius":
    - Pseudonym: wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI
    - Age: 18+
```

If we had a user interface, the redirect URLs would point to a web page, not a web API.

The request ID (`J0uVz49O...`), which is included on the age certificate, is used to make age verification work:

- Crackle will associate this request ID with `publius`.
    - When it receives an age certificate, it will know that the certificate is for `publius`.
- CheckMyAge will associate this request ID with John Smith.
    - When it sends an age certificate, it will put the pseudonym for John Smith on that certificate.

Additionally, a chain of pseudonyms is actually used during age verification:

- the pseudonym stored on CheckMyAge: `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4`
- the pseudonym on the age certificate: `keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA`
- the pseudonym stored on Pop: `wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI`

CheckMyAge never sees the pseudonym stored on Pop, and Pop never sees the pseudonym stored on CheckMyAge.

## Links

- [Design](docs/DESIGN.md) (fairly accessible for a general audience)
- [Architecture](docs/ARCHITECTURE.md) (for engineers)

[rstreet-dne]: https://www.rstreet.org/commentary/the-technology-to-verify-your-age-without-violating-your-privacy-does-not-exist/
