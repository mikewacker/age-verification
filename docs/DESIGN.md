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

TODO

## Age Verification Protocol

TODO

[anonymous-1a]: https://www.mtsu.edu/first-amendment/article/32/anonymous-speech
