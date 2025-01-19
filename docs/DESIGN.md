# Design

## Clarifying Questions

"Design an age verification system."

- A good engineer would not jump straight to a solution here.
- Instead, they would first ask some clarifying questions.

(Asking clarifying question is also a good practice if you're designing an age verification law.)

**Age verification for what? A social media site? An adult site? A gambling site?**

A social media site. Age verification will look different for each type of site:

- To post on social media sites, users typically have to log in to an account.
- While sharing a Netflix account is fairly common, it's not really a viable option to share a social media account.
- (By contrast, most people will browse adult sites anonymously; they won't log in to an account.)

This also leads into the next question...

**Are we just verifying age, or do we also need to verify parental consent?**

Both.

- From a technical perspective, verifying age and verifying parental consent are two very different requirements.
- (The design could be much simpler if we only have to verify age.)
- *Note: A straight ban on social media for kids would mean that we only need to verify age.*

**Will a social media site verify the age themselves, or will they rely on a third-party age verification service?**

Let's go with the third-party option.

- People may not want to give their personal information directly to Facebook or X.
- Instead, they may want to give it to a third party with a singular purpose: age verification.
- A user can verify their age once with this third party, and then use this third party to verify accounts on multiple sites.

**How effective does this system need to be?**

Let's address this talking point: "But kids will find a way to bypass age verification."

- This talking point is technically correct, but practically useless.
- Is it a mere 0.5% of kids that bypass age verification, or is it 50%?

Engineers often do not aim for 100%. Instead, they will talk about the "number of 9s." (E.g., 99.99% is four 9s).

- If the system only "works" for 75% of kids, that would be a great result from a public policy perspective.
- Asking for just one 9 (90%) does seem like a feasible request here.
- In the offline world, IDs are not 100% effective either (e.g., fake IDs).

**What are the privacy concerns here?**

Privacy is important in general, but the main privacy concern will be anonymity:

- Anonymous speech has some [legal protection][anonymous-1a] under the First Amendment.
- The US has a long history of anonymous speech dating back to the *Federalist Papers*.

For the third-party age verification service, another concern will be data retention and data minimization.

- E.g., if a user provides a government ID, a copy of that ID should not be retained after their age is verified.
- What if a person tries to create multiple accounts on the third-party age verification? How do you detect that?
     - You likely will want to retain some information to prevent that scenario from happening.
     - E.g., you may not retain a copy of an ID, but you may retain metadata on which form of age verification was used.

**What about data breaches?**

Let's stipulate that users should not be de-anonymized if(/when) a data breach occurs.

**What sort of burden will age verification put on the user? What is an acceptable burden?**

- It's fine if the user has to do some work on an infrequent basis (e.g., monthly or semi-annually).
- We would want to be more careful about making the user do extra work each time they log in.

**For this proof of concept, which part of this system will we focus on?**

- We'll assume that the third-party age verification service has already verified the age (and guardians) of a person.
- The focus will be on how to anonymously verify a social media account, using this third-party service.
- For verifying parental consent, it's sufficient to verify that one account is the parent of another account.
    - It shouldn't be hard for a site to design a workflow for parental consent once it knows who the parent is.

## Naive Age Verification

There's a saying that you can learn a lot from a wrong answer&mdash;if you understand why it is wrong.

### Naive Protocol

Here is a naive protocol for age verification:

1. You download an age verification certificate from the third-party age verification service.
2. You upload this age certificate to a social media site to verify your account.

The age certificate looks like this:

```json
{
  "ageRange" : {
    "min" : 18
  }
}
```

This certificate essentially says that this person is 18 years or older.

*(While not shown, we'll assume the age certificate is digitally signed so that we can verify its authenticity.)*

### Problems

This is by no means a comprehensive list of problems, but it gives you an idea of just how naive this protocol is.

**Verifying Parental Consent**

- Earlier, we said that we need to verify both age and parental consent.
- This age certificate can only be used to verify a person's age.

**Chain of Custody**

- What if I share this age certificate with someone else (or someone steals it from me)?
- Anyone can use this age certificate, even though it was intended for me.
- I could download three age certificates: one for me, and two for two of my friends.

**Reusing Age Certificates**

- 100 people could use this 1 age certificate to verify 100 different accounts.
- The age certificate is only supposed to verify that 1 of these people is 18 years or older.

**No Expiration**

- Since the age certificate never expires, it can be misused in perpetuity.

**Phishing**

1. John Smith receives an urgent email: "PLEASE RE-VERIFY YOUR AGE OR WE WILL DELETE YOUR ACCOUNT!"
2. He clicks the link in that email, which takes him to a social media site.
3. He uploads his age certificate to that site.
4. That site, however, was a fake social media site; this was a successful phishing attempt.

With this list of problems in mind, the following sections will make a lot more sense.

## Age Certificate

*(Note: The following sections will refer back to the fictional sites and users from the demo.)*

Billy Smith used CheckMyAge to verify his account on Crackle, "publius-jr". The age certificate would look like this:

```json
{
  "request" : {
    "id" : "Ukjls20zalMKr1qqWfdSeX4SOL053vJClscCvuzYLjQ",
    "siteId" : "crackle",
    "expiration" : 1737240263
  },
  "user" : {
    "pseudonym" : "LIQz7hWocXgp1uACRjljzWlD2FTcgSK307Io8l3qvJA",
    "ageRange" : {
      "min" : 13,
      "max" : 17
    },
    "guardianPseudonyms" : [ "keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA" ]
  }
}
```

- `request` is the verification request.
- `user` is the (anonymized) user data.

In addition to the age range (`user > ageRange`), we have five new pieces of data:

- `request > id`
- `request > siteId`
- `request > expiration`
- `user > pseudonym`
- `user > guardianPseudonyms`

### Single-Use Age Certificate

This age certificate can only be used once for a specific site, and it must be used in a timely fashion.

- `request > id` is a nonce: number used only once.
    - Each age certificate will have a different ID.
    - If a site sees two age certificates with the same ID, it will know that the same age certificate is being reused.
- `request > siteId` ensures that the age certificate is used for the correct site.
    - This age certificate can be used to verify an account on Crackle.
    - It would be rejected if it was used for Pop, since the `siteId` is `crackle`.
- `request > expiration` is an expiration date.
    - This number is what's called a timestamp; `1737240263` is Jan 18, 2025, 3:44:23 PM (Mountain Time).

(`request > id` has another use that we'll discuss later.)

### Pseudonyms

A pseudonym (`user > pseudonym`) is used to anonymously identify a person. It serves two purposes:

- **One person can only verify one account for a site.**
    - If two accounts have the same pseudonym, the site can detect an attempt by one person to verify two accounts.
- They can be used to anonymously verify that one account is the guardian of another account.
    - The value in `user > guardianPseudonyms` is the pseudonym of the guardian.

**Pseudonyms and Data Breaches**

A social media site will store the pseudonym that is linked to each account.

- What if a data breach reveals the pseudonym of an account?
    - On the age verification service (CheckMyAge), a pseudonym will be linked to John Smith.
    - On a social media site (Crackle), a pseudonym will be linked to "publius" (John Smith's account).
    - If the same pseudonym is used for CheckMyAge and Crackle, that pseudonym can link John Smith to "publius".
- Solution: A person will have a different pseudonym for each social media site (and for the age verification site).
- E.g., in the demo, John Smith's pseudonym was...
    - `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4` on CheckMyAge.
    - `wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI` on Crackle.
    - `Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE` on Pop.

## Age Verification Protocol

### Workflow

**Requirements**

To make strong guarantees of anonymity, the protocol will have these requirements:

- The social media site can never learn the real name of the person who is verifying an account.
- The age verification service can never learn which account it is verifying for a social media site.

The `request > id` will play a crucial role in fulfilling both requirements.

**Protocol: Walkthrough**

Let's walk through how Billy Smith verifies his account on Crackle, "publius-jr":

I. After logging in to Crackle, Billy Smith starts the process to verify "publius-jr".

1. Crackle asks CheckMyAge to create a verification request.
2. CheckMyAge creates a verification request with a random ID (e.g., `Ukjls20z...`).
    - The expiration will be, e.g., five minutes from now.

```json
{
  "id" : "Ukjls20zalMKr1qqWfdSeX4SOL053vJClscCvuzYLjQ",
  "siteId" : "crackle",
  "expiration" : 1737240263
}
```

3. CheckMyAge stores this verification request.
4. CheckMyAge sends this verification request to Crackle.
5. Crackle links the request ID (`Ukjls20z...`) to "publius-jr".
6. Crackle redirects the user to CheckMyAge; the URL will contain the request ID (`Ukjls20z...`).

II. Billy Smith logs in to CheckMyAge (if needed) after being redirected there.

1. CheckMyAge gets the request ID (`Ukjls20z...`) from the URL.
2. CheckMyAge loads the verification request with this ID.
    - In step I.3., CheckMyAge stored this verification request.

```json
{
  "id" : "Ukjls20zalMKr1qqWfdSeX4SOL053vJClscCvuzYLjQ",
  "siteId" : "crackle",
  "expiration" : 1737240263
}
```

3. CheckMyAge links this verification request to Billy Smith.

III. Billy Smith confirms with CheckMyAge that he wants to send an age certificate to Crackle.

1. CheckMyAge loads the verification request that is linked to Billy Smith:
    - In step II.3., CheckMyAge linked this verification request to Billy Smith.

```json
{
  "id" : "Ukjls20zalMKr1qqWfdSeX4SOL053vJClscCvuzYLjQ",
  "siteId" : "crackle",
  "expiration" : 1737240263
}
```

2. CheckMyAge loads the (anonymized) user data for Billy Smith.

```json
{
  "pseudonym" : "KB0b9pDo8j7-1p90fFokbgHj8hzbbU7jCGGjfuMzLR4",
  "ageRange" : {
    "min" : 13,
    "max" : 13
  }, 
  "guardianPseudonyms" : [ "uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4" ]
}
```

3. CheckMyAge "localizes" the user data. (The next section will explain the "localization" process.)

```json
{
  "pseudonym" : "LIQz7hWocXgp1uACRjljzWlD2FTcgSK307Io8l3qvJA",
  "ageRange" : {
    "min" : 13,
    "max" : 17
  }, 
  "guardianPseudonyms" : [ "keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA" ]
}
```

4. CheckMyAge combines the verification request and the user data to create an age certificate.

```json
{
  "request" : {
    "id" : "Ukjls20zalMKr1qqWfdSeX4SOL053vJClscCvuzYLjQ",
    "siteId" : "crackle",
    "expiration" : 1737240263
  },
  "user" : {
    "pseudonym" : "LIQz7hWocXgp1uACRjljzWlD2FTcgSK307Io8l3qvJA",
    "ageRange" : {
      "min" : 13,
      "max" : 17
    },
    "guardianPseudonyms" : [ "keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA" ]
  }
}
```

5. CheckMyAge digitally signs the age certificate and sends it to Crackle.
6. Crackle validates the age certificate it received (e.g., signature is valid, age certificate is not expired, etc.).
7. Crackle uses the request ID (`Ukjls20za...`) on the age certificate to determine that it is for "publius-jr".
    - In step I.5., Crackle linked this request ID to "publius-jr".
8. Crackle "localizes" the user data on the age certificate.

```json
{
  "pseudonym" : "vT47RJUVsiagXQvHACvJKjliGLM97QcBrFRk9PfmAxE",
  "ageRange" : {
    "min" : 13,
    "max" : 17
  },
  "guardianPseudonyms" : [ "wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI" ]
}
```

9. Crackle stores this user data for "publius-jr"; the age range and guardians of "publius-jr" are now verified.

(There's a few thing that could be done to improve the protocol, but that's the basic idea.)

### "Localizing" Pseudonyms

**Requirements**

To make strong guarantees of anonymity, the protocol will have these requirements:

- The age verification service never sees the pseudonym that is stored on the social media site.
- The social media site never sees the pseudonym that is stored on the age verification service.

**Overview**

To verify Billy Smith's account on Crackle, "publius-jr", the protocol used three different pseudonyms:

|                              | `KB0b9pDo...` | `LIQz7hWo...` | `vT47RJUV...` |
|------------------------------|---------------|---------------|---------------|
| **Stored on CheckMyAge**     | ✅             | ❌             | ❌             |
| **Seen by CheckMyAge**       | ✅             | ✅             | ❌             |
| **Used for age certificate** | ❌             | ✅             | ❌             |
| **Seen by Crackle**          | ❌             | ✅             | ✅             |
| **Stored on Crackle**        | ❌             | ❌             | ✅             |

**Keys**

As part of the protocol, each site will have two keys (or secret values).

- One key will be stored on the age verification service; only the age verification service knows this key.
- The other key will be stored on the social media site; only the social media site knows this key.

These keys will be used to transform a pseudonym; we'll call this transformation "localization".

**Protocol**

Here is the user data stored for Billy Smith on CheckMyAge:

```json
{
  "pseudonym" : "KB0b9pDo8j7-1p90fFokbgHj8hzbbU7jCGGjfuMzLR4",
  "ageRange" : {
    "min" : 13,
    "max" : 13
  }, 
  "guardianPseudonyms" : [ "uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4" ]
}
```

Before it generates an age certificate for Crackle, CheckMyAge will change this data in two ways:

- It will "localize" any pseudonyms using its key.
    - Corollary: Crackle never sees the pseudonyms that are stored on CheckMyAge.
    - (Note: CheckMyAge uses a different key for each site.)
- It will change the age from an exact value to an age range.
    - Crackle will need to tell CheckMyAge which age ranges it cares about (e.g., 12-, 13-17, 18+).

Here is the user data that is displayed on the age certificate:

```json
{
  "pseudonym" : "LIQz7hWocXgp1uACRjljzWlD2FTcgSK307Io8l3qvJA",
  "ageRange" : {
    "min" : 13,
    "max" : 17
  }, 
  "guardianPseudonyms" : [ "keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA" ]
}
```

Before Crackle stores this data, it will change it in one way:

- It will "localize" any pseudonyms using its key.
    - Corollary: CheckMyAge never sees the pseudonyms that are stored on Crackle.

Here is the data stored for "publius-jr" on Crackle:

```json
{
  "pseudonym" : "vT47RJUVsiagXQvHACvJKjliGLM97QcBrFRk9PfmAxE",
  "ageRange" : {
    "min" : 13,
    "max" : 17
  },
  "guardianPseudonyms" : [ "wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI" ]
}
```

**De-Anonymization**

Is it possible to de-anonymize users?

- If you know the pseudonym of "publius-jr" (`vT47RJUV...`), you cannot de-anonymize "publius-jr".
- If you know the pseudonym of Billy Smith (`KB0b9pDo...`), you would have to steal both keys to de-anonymize "publius-jr".
    - It suffices to say that stealing keys from two separate sites would be a very tall order.
    - Even if you stole both keys, you could only de-anonymize users on Crackle, not on any other social media site.
    - Crackle would have some culpability in this (highly unlikely) "dual key breach" scenario.

I'm not sure how they protected Publius's identity for the *Federalist Papers*, but this is probably more secure than that.

**Downside**

If you cycled in new keys, that would change the pseudonym for each account.

- A site would have to make every user re-verify their account (with some grace period) after new keys are cycled in.
- But since this only happens annually or semi-annually, this is not a big deal.

[anonymous-1a]: https://www.mtsu.edu/first-amendment/article/32/anonymous-speech
