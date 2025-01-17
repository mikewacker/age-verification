# Proof-of-Concept: Privacy-Conscious Age Verification

As age verification for social media becomes more popular with federal and state lawmakers, some tech policy experts
[have said][rstreet-dne], "The technology to verify your age without violating your privacy does not exist."
But are those experts right? Rather than tell you, I will show you with a demo:

## Demo

### Anonymous Age Verification

Here is the setup:

- John and Billy Smith have used CheckMyAge, a third-party age verification service, to verify that...
    - John Smith's age is 40.
    - Billy Smith's age is 13.
    - John Smith is Billy Smith's father.
- John and Billy Smith need to anonymously verify their accounts on Crackle: "publius" and "publius-jr".

*(Note: The demo doesn't have a user interface; text will narrate what happens as we communicate with these sites.)*

Let's start with Billy Smith:

```text
John Smith uses CheckMyAge to verify his account on Crackle, "publius".
- On Crackle, John Smith begins the process to verify "publius".
- (Behind the scenes, Crackle contacts CheckMyAge.)
    - Crackle does NOT share the account name, "publius", with CheckMyAge.
- Crackle redirects John Smith to CheckMyAge.
- John Smith confirms with CheckMyAge that he wants to verify an account on Crackle.
    - CheckMyAge does NOT know which account on Crackle is being verified.
- (Behind the scenes, CheckMyAge sends an age certificate to Crackle.)
    - CheckMyAge does NOT share John Smith's real name with Crackle.
- CheckMyAge redirects John Smith to Crackle.
"publius" is verified on Crackle:
{
  "pseudonym" : "wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI",
  "ageRange" : {
    "min" : 18,
    "max" : null
  },
  "guardianPseudonyms" : [ ]
}
```

Let's summarize what happened:

- "publius" is identified not by a real name, but by a pseudonym: `wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI`.
- Crackle knows that the age range of "publius" is 18+.
- Crackle never learned the real name of "publius".
- CheckMyAge never learned which account it was verifying on Crackle.

Next, let's verify Billy Smith:

```text
Billy Smith uses CheckMyAge to verify his account on Crackle, "publius-jr".
"publius-jr" is verified on Crackle:
{
  "pseudonym" : "vT47RJUVsiagXQvHACvJKjliGLM97QcBrFRk9PfmAxE",
  "ageRange" : {
    "min" : 13,
    "max" : 17
  },
  "guardianPseudonyms" : [ "wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI" ]
}
```

Let's summarize what happened:

- Crackle knows that the age range of "publius-jr" is 13-17.
- Crackle knows that the guardian of "publius-jr" is "publius".
    - The value in `guardianPseudonyms` (`wqhgWlb9...`) matches the pseudonym of "publius".
- As before, everything was done anonymously.

### What About Data Breaches?

To answer that question, let's have John and Billy Smith verify their age on another social media site, Pop:

```text
John Smith uses CheckMyAge to verify his account on Pop, "JohnS".
"JohnS" is verified on Pop:
{
  "pseudonym" : "Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE",
  "ageRange" : {
    "min" : 18,
    "max" : null
  },
  "guardianPseudonyms" : [ ]
}

================================================================

Billy Smith uses CheckMyAge to verify his account on Pop, "BillyS".
"BillyS" is verified on Pop:
{
  "pseudonym" : "5L3siUtZ5-HAKlxMGjwvEjFB49FoLJdSpytsuE6WeQU",
  "ageRange" : {
    "min" : 13,
    "max" : 17
  },
  "guardianPseudonyms" : [ "Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE" ]
}
```

Let's summarize what happened:

- The pseudonyms used on Pop are different from the pseudonyms used on Crackle.
- While not shown, those pseudonyms are also different from the pseudonyms used on CheckMyAge.
- To use John Smith as an example, his pseudonym is...
    - `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4` on CheckMyAge.
    - `wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI` on Crackle.
    - `Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE` on Pop.

Even if a data breach occurred on every site, you still could not figure out that John Smith is "publius".

## Links

- [Design](docs/DESIGN.md) (fairly accessible for a general audience)
- [Architecture](docs/ARCHITECTURE.md) (for engineers)

[rstreet-dne]: https://www.rstreet.org/commentary/the-technology-to-verify-your-age-without-violating-your-privacy-does-not-exist/
