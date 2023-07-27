# Proof-of-Concept: Privacy-Conscious Age Verification

*(To take a quick look, you can look at the overview and the high-level workflow for the demo&mdash;and
maybe the entire demo.)*

## Overview

As age verification mandates for social media have gained traction on both the state and federal level,
some tech policy experts have said that age verification and privacy cannot coexist.
[This quote][rstreet-dne] is a representative example of what those experts have said:
"The technology to verify your age without violating your privacy does not exist."

But are those experts right? Simply put, no. But rather than tell you why, I will show you&mdash;by
writing proof-of-concept code for privacy-conscious age verification:

- It provides robust age verification, with multiple measures to minimize the number of kids who bypass the system.
- For anonymous accounts, it verifies their age without de-anonymizing them, via multiple layers of security.
- It minimizes the impact of data breaches; users won't get de-anonymized if a data breach occurs.

Any tradeoffs were weighed heavily in favor of privacy. The system is not designed to be 100% effective,
but it will still be very effective&mdash;while protecting the anonymity of users.

## Demo

### High-Level Workflow

Since it's easier to show than tell, let's run a demo.
This demo will run the code, and it will display some text to explain what is happening.

Here is the first part of the demo, where a parent and child verify their accounts on two social media sites:

```
In this proof-of-concept:
- CheckMyAge is a third-party age verification service.
- Crackle and Pop are social media sites that have registered with CheckMyAge.
- Crackle and Pop need to know whether a user's age is 12-, 13-17, 18+.

John Smith has already verified his identity on CheckMyAge:
- Age: 40
Bobby Smith has already verified his identity on CheckMyAge:
- Age: 13
- Guardian: John Smith

John Smith uses CheckMyAge to verify "JohnS" on Crackle:
- Age: 18+
Bobby Smith uses CheckMyAge to verify "BobbyS" on Crackle:
- Age: 13-17
- Guardian: JohnS

John Smith uses CheckMyAge to verify "publius" on Pop:
- Age: 18+
Bobby Smith uses CheckMyAge to verify "publius-jr" on Pop:
- Age: 13-17
- Guardian: publius
```

In the second part of the demo, we'll look slightly under the hood to see what data is being stored:

```
Data stored on CheckMyAge:
- John Smith:
    - ID: uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4
    - Age: 40
- Bobby Smith:
    - ID: KB0b9pDo8j7-1p90fFokbgHj8hzbbU7jCGGjfuMzLR4
    - Age: 13
    - Guardian ID: uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4

Data stored on Crackle:
- JohnS:
    - ID: wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI
    - Age: 18+
- BobbyS:
    - ID: vT47RJUVsiagXQvHACvJKjliGLM97QcBrFRk9PfmAxE
    - Age: 13-17
    - Guardian ID: wqhgWlb9wYtzTDYbGeYFJJvS4xjmQsp3cf3ntbcBuNI

Data stored on Pop:
- publius:
    - ID: Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE
    - Age: 18+
- publius-jr:
    - ID: 5L3siUtZ5-HAKlxMGjwvEjFB49FoLJdSpytsuE6WeQU
    - Age: 13-17
    - Guardian ID: Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE
```

The IDs serve three purposes:

1. They provide anonymity; CheckMyAge only shares IDs and age ranges (e.g., 13-17, 18+) with Crackle and Pop.
2. They verify that one user is the guardian of another user&mdash;without revealing the identity of those users.
3. They ensure that one person cannot verify multiple accounts on a single site.

Each person also has a different ID for CheckMyAge, Crackle, and Pop:

- If both CheckMyAge and Pop suffered data breaches, you could not figure out that `publius` is John Smith.
- If both Crackle and Pop suffered data breaches,
  you could not figure out that `JohnS` and `publius` are the same person.

(If one engineer could accomplish this, imagine what a team of engineers with even more expertise could accomplish.)

### Detailed Workflow

The third part of the demo looks at the workflow to verify an account in more detail:

```
Detailed workflow to verify "publius-jr" on Pop:
- The current time is July 25, 2023, 12:42:56 PM MDT.
- [#1], [#2] is used to denote when data is stored that will be used later.

For a proof-of-concept, everything runs on a single machine;
the demo pretends that CheckMyAge and Pop are separate websites.
(For the real thing, we assume that engineers know how to build a website.)

Part I
- Bobby Smith logs in as "publius-jr" on Pop.
- Bobby Smith starts the process to verify "publius-jr".
- Pop asks CheckMyAge to create a new verification request.
- CheckMyAge generates the following verification request:
    - Request ID: 7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk
    - Site: Pop
    - Expiration: July 25, 2023, 12:47:56 PM MDT
- [#1] CheckMyAge stores a copy of the verification request.
- CheckMyAge sends the verification request back to Pop.
- [#2] Pop links the request ID (7-DQzGbL...) to "publius-jr".
- Pop opens the following URL in a new window on Bobby Smith's browser:
  https://www.checkmyage.com/verify/7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk

Part II
- CheckMyAge receives a web request at the following URL:
  https://www.checkmyage.com/verify/7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk
- CheckMyAge gets the verification request ID (7-DQzGbL...) from the URL.
- [#1] CheckMyAge retrieves the full verification request:
    - Request ID: 7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk
    - Site: Pop
    - Expiration: July 25, 2023, 12:47:56 PM MDT
- CheckMyAge checks that the verification request is not expired.
- CheckMyAge responds to the web request by displaying a login page.
- Bobby Smith logs in to CheckMyAge.
- CheckMyAge links the request ID (7-DQzGbL...) to Bobby Smith.
- CheckMyAge confirms that Bobby Smith wants to verify an account on Pop.
- CheckMyAge retrieves the user data for Bobby Smith:
    - ID: KB0b9pDo8j7-1p90fFokbgHj8hzbbU7jCGGjfuMzLR4
    - Age: 13
    - Guardian ID: uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4
- CheckMyAge anonymizes the age that it will share with Pop: 13-17
- CheckMyAge creates new IDs from the original IDs and its secret key for Pop.
- Updated user data:
    - ID: A8y9RGWwLiwhZSaX0i_TZhyX-2r9DxMmrrngoADCUhE
    - Age: 13-17
    - Guardian ID: iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4
- CheckMyAge creates the age certificate:
    - Request ID: 7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk
    - Site: Pop
    - Expiration: July 25, 2023, 12:47:56 PM MDT
    - ID: A8y9RGWwLiwhZSaX0i_TZhyX-2r9DxMmrrngoADCUhE
    - Age: 13-17
    - Guardian ID: iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4
- CheckMyAge digitally signs the age certificate.
- CheckMyAge securely transmits the signed age certificate to Pop.

Part III
- Pop receives a signed age certificate:
    - Request ID: 7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk
    - Site: Pop
    - Expiration: July 25, 2023, 12:47:56 PM MDT
    - ID: A8y9RGWwLiwhZSaX0i_TZhyX-2r9DxMmrrngoADCUhE
    - Age: 13-17
    - Guardian ID: iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4
- Pop verifies the signed age certificate.
    - It verifies that the age certificate is signed by CheckMyAge.
    - It verifies that Pop is the recipient in the "Site" field.
    - It verifies that the age certificate is not expired.
- [#2] Pop matches the request ID (7-DQzGbL...) to "publius-jr".
- Pop extracts the user data from the age certificate:
    - ID: A8y9RGWwLiwhZSaX0i_TZhyX-2r9DxMmrrngoADCUhE
    - Age: 13-17
    - Guardian ID: iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4
- Pop creates new IDs from the original IDs and a secret key.
- Updated user data:
    - ID: 5L3siUtZ5-HAKlxMGjwvEjFB49FoLJdSpytsuE6WeQU
    - Age: 13-17
    - Guardian ID: Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE
- Pop checks that no other accounts have the same user ID (5L3siUtZ...).
- Pop stores this user data for "publius-jr". "publius-jr" is now verified!
```

*(If you run the demo yourself, the times and request ID will be different.)*

Notes:

- Communication between CheckMyAge and Pop is mediated via a request ID, which is randomly generated.
    - Pop never shares the username (`publius-jr`) with CheckMyAge.
    - CheckMyAge never shares the real name (Bobby Smith) with Pop.
- The age certificate contains an "intermediate ID."
    - CheckMyAge never sees the ID stored on Pop (`5L3siUtZ...`).
    - Pop never sees the ID stored on CheckMyAge (`KB0b9pDo...`).

## Requirements

In the real world, you need to show an ID to buy alcohol. And of course, some underage people find ways to bypass that,
such as using a fake ID. But should we abandon ID checks altogether because they are not 100% effective? Of course not.

Even for critical systems, software engineers do not aim for 100%. Instead, they will talk about the "number of 9s."
E.g., five 9s means that the system works 99.999% of the time.

In general, 100% and/or perfection is not a goal.

### High-Level Idea

Many social media accounts are anonymous; protecting the anonymity of such accounts ia a key consideration.

- For protecting the anonymity of users, we want an "A" grade.
- For stopping kids from bypassing the system, we want a "B" or "C" grade.

It certainly could be possible to do better; these grades are used to show how we will weigh any tradeoffs involved.

### High-Level Requirements

**Type of Site**

We will focus on social media sites (i.e., not on adult content sites):

- To post on social media sites, users typically have to log in to an account.
- While sharing a Netflix account is fairly common, it's not really a viable option to share a social media account.
- While minors are allowed, we will need to verify both their age and who their parents(/guardians) are.

(That's not to say that age verification cannot be done for adult content sites,
but the requirements&mdash;and thus the solution&mdash;will be different.)

**First-Party or Third-Party**

For this proof-of-concept, we will assume that a social media site uses a third-party age verification service.

It suffices to say that you may not want Facebook or Twitter to collect even more personal information from you,
only to use it for other purposes. Instead, it would be better to provide such information to a trusted third party
that has a singular purpose: age verification.

(If a site does build a first-party age verification service, they could establish internal firewalls
that separates the team running that service from the rest of the company.)

**Workflows**

For a third-party age verification service, there are two workflows to consider:

1. A person registers and verifies their identity on the third-party age verification service.
2. A person uses that age verification service to verify their account on a social media site.

We will discuss both workflows, but the focus will be on the second workflow.
Likewise, all the code written was for the second workflow.

The second workflow is more challenging: the age verification service needs to communicate
with a social media site&mdash;without revealing information that could de-anonymize a user.

### Detailed Requirements

**Effectiveness**

Let's address this talking point: ...but kids will find ways to bypass age verification.

That statement is technically correct, but practically useless. Is it a mere 0.5% of kids that bypass age verification,
or is it 50%?

We certainly do not need five 9s here, and we may not even need one 9.
E.g., if you can "only" reduce the number of minors on social media without parental supervision by 75%,
that would be a fantastic result. (Nonetheless, a certain class of experts will endlessly harp
about the 25% of kids who do bypass the system.)

**Anonymity**

Anonymity will be extremely important for privacy, legal, and historical reasons.

Anonymous speech does have some [legal protection][anonymous-1a] under the First Amendment,
and the US has a long history of anonymous speech dating back to the *Federalist Papers*&mdash;which
were written by three Founding Fathers (Alexander Hamilton, James Madison, and John Jay)
under the pseudonym Publius. Thus, the bar will be set very high.

Nonetheless, perfection is not a goal. While the identity of Publius was a closely guarded secret back then,
surely there was a non-zero risk that the identity of Publius could be revealed.
If the odds of de-anonymizing an account today are much lower than the odds of de-anonymizing Publius back then,
then I'd say that we have done an excellent job.

**Extra Work**

How much extra work will be involved in verifying a social media account? That boils down to two question:

1. How much work is involved in verifying a social media account?
2. How often do you have to verify that account?

Let's focus on that second question:

- If an account has to be verified every year or every six months, that's fine.
  E.g., some websites will make you change your password once a year.
- We would like to avoid making a user verify their account every time they log in.
- Making users verify their account on a more frequent basis (e.g., weekly, every other week, monthly)
  can be considered, depending on the benefits.

**Data Breaches**

With any breach, there are two risks to consider:

- exposing sensitive personal information about a person
- revealing information that can be used to de-anonymize a social media account

Let's just stipulate that if your account can get de-anonymized if(/when) a data breach occurs, that's a fatal flaw.

---

However, there is some nuance to this conversation. In short, there are two types of breaches to consider:

- data breach: a breach that exposes user data
- key breach: a breach that exposes secret keys for your site

(E.g., here is a mostly correct explanation of what happens when you visit `https://www.amazon.com`;
the `s` in `https` stands for secure. Your web browser will use Amazon's public key
to encrypt any data that is sent to Amazon. To decrypt that data,
you would need to use Amazon's private key&mdash;which only Amazon knows.)

Data breaches are far more common than key breaches, and most cybersecurity incidents in the news are data breaches.
We will mostly focus on mitigating the impact of a data breach, though we will consider both types of breaches.

## Detailed Design

### Format

There's a saying that you can learn a lot from a wrong answer&mdash;if you understand why it is wrong.

With that in mind, we will use the following format for the detailed design:

- First, we will nail down the requirements for age verification. (We have already done this.)
- Then, we will start with a naive, v0 (version 0) solution for age verification.
    - You can think of the v0 solution as an "intentionally wrong" answer.
- We will incrementally improve this solution, creating v1, v2, v3, etc. For each version:
    - We will identify a problem (or problems) with the current version.
    - To create the next version, we will develop a solution to said problem.
    - At each step, we will also discuss any tradeoffs incurred, and any other relevant considerations.

This detailed design will teach you how to think about age verification, not just tell you what to think.

I have tried to make this explanation fairly accessible for a non-technical audience;
sections aimed at a more technical audience will be tagged as [Technical].

### v0: Naive Age Verification

Let's discuss how these two workflows would work in a naive solution.
(For the sake of clarity, we will use the fictional names from the demo here.)

Registering on CheckMyAge:

- John Smith creates an account on CheckMyAge.
- John Smith uploads photocopies of documents (e.g., a driver's license) to CheckMyAge.
    - These documents are stored forever on CheckMyAge.
- CheckMyAge uses these documents to verify John Smith's identity and age.

Verifying an account on Pop:

- John Smith downloads a signed age certificate from CheckMyAge. Contents of certificate:
    - Age: 40
- John Smith logs in as `publius` on Pop.
- John Smith uploads the age certificate to Pop.
- Pop verifies that the age certificate is signed by CheckMyAge.
- Pop has now verified the age of `publius`.

**[Technical] Signing Key**

The proof-of-concept uses an Ed25519 key pair to digitally sign an age certificate,
though other excellent choices exist, such as NIST P-256.

(For the real thing, you would also need a digital certificate to prove that CheckMyAge owns the public key.)

### v1: Setting a Data Retention Policy

**Problem: Stored Documents and Data Breaches**

CheckMyAge stores documents it receives forever, but does it need to do so?
If a data breach occurs, a lot of documents and sensitive information can be stolen from CheckMyAge.

**Solution: Data Minimization**

Once John Smith's identity is verified, documents or sensitive information (e.g., a Social Security number)
should not be retained indefinitely. At most, they should be retained for a short time (e.g., one week).

We only need to retain some basic information that can establish John Smith's identity: full name, date of birth, etc.
While we won't get into granular specifics here, we can describe the primary purpose of retaining data&mdash;which
will guide any data minimization efforts.

In short, John Smith should not be able to create two accounts on CheckMyAge.
We should only retain enough data to detect when two accounts are actually the same person.
We don't need to be 100% perfect here, but if the percentage of accounts that are not duplicates
can be measured with two or even three 9s, that would be an achievable goal.

**Tradeoff: Re-Verifying Users**

If CheckMyAge needs to re-verify the identity of John Smith, he will need to re-upload those documents.
It's a minor inconvenience, but it's a small price to pay for data minimization.

**Other Consideration: Auditing**

What if an auditor wants to come in and check that CheckMyAge isn't fraudulently verifying accounts?
There are a couple of ways to accommodate auditors:

- If we have a short retention window for documents, auditors can audit any accounts that were recently verified.
- We could retain metadata about which documents were used. Auditors could then randomly select users,
  and ask them to re-verify their account with the same documents.

Here, CheckMyAge should set the expectation that users may need to re-verify their account&mdash;and
that noncompliance with such requests will un-verify their account.
A small percentage of users may still be unresponsive to requests to re-verify their account,
but if that percentage gets too large, that would be a red flag for auditors.

### v2: Hardening Age Certificates

**Problem: Sharing or Stealing Age Certificates**

What if John Smith decides to share his age certificate with someone else?
(Or, what if hackers steal an age certificate from John Smith?)

Anyone can use that age certificate to verify their account, not just John Smith.

**Solution: Nonces and Expirations**

To mitigate this problem, we can make a few additions to the age certificate:

- a nonce (number used once), which will ensure that a certificate can only be used once on a single site.
    - Each certificate will have a difference nonce.
    - If a site receives two certificates with the same nonce, it would know that one certificate has been used twice.
    - (However, a single certificate can be used for multiple sites&mdash;but only one time for each site.)
- an expiration; certificates past the expiration will be rejected.
    - The expiration can be set to, e.g., 5 minutes after the certificate is generated.

### v3: Adding IDs (Not Real Names or Usernames) to an Age Certificate

**Problem: Sharing Age Certificates**

While we did limit the impact of sharing (or stealing) a single age certificate,
John Smith could easily work around that by downloading a second age certificate from CheckMyAge.

**Problem: Verifying Guardian Relationships**

Let's say that John Smith's son, Bobby Smith, also verifies his age on Pop.
In this case, we also need to verify that John Smith is Bobby Smith's parent. How do we do that?

**Problem Analysis: Authentication or Deduplication?**

At first glance, the first problem may seem to be an authentication problem.
If John Smith downloads an age certificate from CheckMyAge,
Pop needs to verify that the person who uploads that certificate is John Smith.

But what if we instead viewed it as a deduplication problem? If Pop receives three age certificates,
how can it tell whether they came from three people who each downloaded one age certificate,
or from one person who downloaded three age certificates&mdash;and shared two of them with two of his friends?

**Solution: Use IDs (Instead of Real Names or Usernames)**

CheckMyAge can assign an ID (e.g., `uhzmISXl...`) to each person, and include that ID on the age certificate.
Since each person only has a single account on CheckMyAge, that also means that each person only has a single ID.
(A few accounts on CheckMyAge may be duplicate accounts, but this happens less than 1% or even 0.1% of the time.)

If John Smith downloads three age certificates&mdash;one for himself and two for two of his friends&mdash;Pop
will receive three age certificates with the same ID.

Likewise, we can use IDs to specify that one user is the guardian of another user.
Instead of saying that John Smith is the guardian of Bobby Smith,
we can say that the user with ID `uhzmISXl...` is the guardian of the user with ID `KB0b9pDo...`.
For minors, their age certificate would also include the IDs of any guardians.

**Solution: ID Design**

We also need to think carefully about how CheckMyAge assigns IDs, with two privacy/security considerations in mind:

- The IDs should not reveal any information about a person.
- The IDs should be resistant to brute-force attacks (i.e., an attack where you try every single ID).

Here are some examples of how not to assign IDs:

- Assign IDs sequentially (1, 2, 3, ...). That would reveal some information about when a person created their account.
- Use a Social Security number as an ID. Since there are only 1,000,000,000 possible IDs,
  any brute-force attack that tries every possible ID is feasible.

If you looked closely at the IDs in the demo, you may have noticed a few things:

- An ID has 43 characters.
- A single character has 64 possible values: lowercase letters (26), uppercase letters (26), numbers (10), `-`, and `_`.

It's mostly correct to say that an ID is 43 randomly chosen characters.
There are approximately 10<sup>77</sup> possible IDs (i.e., 1 followed by 77 0s).
By comparison, scientists estimate that there are 10<sup>80</sup> atoms in the universe.
It suffices to say that any brute-force attack that tries every possible ID would be infeasible.

**[Technical] Solution: ID Design, Continued**

An ID is actually 256 randomly generated bits; we use a URL-friendly base64 encoding to convert it to text.

Any time you randomly generate bits, you should use a cryptographically strong random number generator
(e.g., Java's [`SecureRandom`][secure-random]), not a normal random number generator.

**Tradeoff: Deduplication vs. Authentication**

If John Smith does not have his own account on Pop, his account on CheckMyAge could be used
to verify someone else's account on Pop&mdash;although it can still only be used to verify one account.
That problem is an authentication problem.

In terms of stopping kids from bypassing the system, this solution may not be an "A" solution for that reason,
but it still would receive a good grade. Its key advantage, though, is that it uses an anonymous ID&mdash;as
opposed to a real name or a username.

Likewise, since this problem occurs under a much narrower set of conditions,
we can pursue more incremental fixes to mitigate the problem going forward.
Since we already have a good grade, we only need to find the incremental fixes that slightly increase that grade.

**Other Consideration: Mitigating a Smaller Authentication Problem**

Let's assume that John Smith does not have his own account on Pop.
Let's also assume that there's a misbehaved kid, Bobby Tables, who wants to verify his account on Pop (`injector`)
using John Smith's age certificate. We'll consider two variants of that scenario:

1. John Smith is willing to help Bobby Tables bypass age verification.
2. Bobby Tables is willing to resort to subterfuge.

We can break this problem down into three sub-problems. We will briefly analyze each sub-problem:

1. Bobby Tables logs into John Smith's account on CheckMyAge, either with or without his permission.
    - Offering or even requiring two-factor authentication can keep unauthorized users out of John Smith's
      account&mdash;and makes it harder for him to let others use his account.
    - Other mitigations are possible, but we would have to weigh the privacy tradeoffs&mdash;especially
      with respect to what additional information is stored on CheckMyAge.
        - On the plus side, CheckMyAge doesn't share additional information with Pop.
          If it believes that the person is not John Smith, it simply would not issue an age certificate.
2. John Smith downloads an age certificate, which he shares with Bobby Tables. (Or, Bobby Tables steals it.)
    - Pop could periodically make users re-verify their account.
        - John Smith may be willing to help Bobby Tables once, but not on a reoccurring basis.
        - Bobby Smith might successfully employ subterfuge once, but could he do so on a reoccurring basis?
        - There will be a tradeoff, though; making users re-verify too frequently could become a major inconvenience.
    - (In the next version, we will trade this problem for a similar but different problem that is easier to mitigate.) 
3. Bobby Tables shares the password for `injector` with John Smith, so that John Smith can verify his account.
    - As before, Pop could periodically make users re-verify their account.

### v4: Directly Transmitting Age Certificates to Sites

**Problem: Phishing for Age Certificates&mdash;and for IDs**

John Smith opens his email and sees an urgent email: "PLEASE RE-VERIFY YOUR AGE ON POP OR WE WILL DELETE YOUR ACCOUNT!"
He clicks the link in that email, which takes him to a site that looks like Pop.
He uploads his age certificate to that site. That site, however, was a fake site that looks like Pop.

This was a successful phishing attempt; a scammer just obtained John Smith's ID.
Moreover, when John clicked the link, his email was passed onto the fake site; his ID is now linked to an email.
This fake site may have also asked John to provide additional personal information as well.

Alternatively, if a hacker steals John's password for CheckMyAge, he could log in and download an age certificate.
Now he has John Smith's ID, and that ID is linked to his real name.

**Problem: Sharing Age Certificates**

If John Smith downloads an age certificate from CheckMyAge, how do we stop him from sharing it with someone else?

**Problem: Age and Data Minimization**

CheckMyAge shares John Smith's exact age (40) with Pop, but does Pop need to know his exact age?
Pop only needs to know that John Smith is 18 or older.

**Solution: Directly Transmit the Age Certificate to the Site**

We will set up a workflow where CheckMyAge directly transmits an age certificate to Pop.

First, Pop will need to register with CheckMyAge. As part of this registration, it will tell CheckMyAge:

- the age ranges that Pop cares about
    - Let's say that Pop cares about these ranges: 12-, 13-17, 18+.
    - CheckMyAge will include an age range on the age certificate, instead of the exact age.
- a URL (starting with `https://`) that CheckMyAge can use to securely transmit certificates to Pop

The new age verification protocol will be described below. While it's more complex,
most of the work is handled by CheckMyAge and Pop; there's less work for John Smith.

---

This protocol will rely on a "verification request"; this request is needed to create an age certificate.
Here are the contents of the verification request from the demo:

- Request ID: `7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk`
- Site: Pop
- Expiration: July 25, 2023, 12:47:56 PM MDT

CheckMyAge and Pop will share this verification request with each other; they will not share real names or usernames.

---

Let's start with the process to create a verification request.

- John Smith starts the age verification process for `publius` on Pop.
- Pop asks CheckMyAge to generate a new verification request.
- CheckMyAge generates a verification request with the "Site" set to Pop.
- CheckMyAge stores a copy of the verification request.
- CheckMyAge sends the verification request back to Pop.
- Pop links the verification request to `publius`.

Notes:

- Only Pop can create a verification request (with some assistance from CheckMyAge).
    - Since Pop has to register with CheckMyAge, we can set up a way for Pop to authenticate itself with CheckMyAge.
    - CheckMyAge controls both the "Request ID" and the "Expiration".
- Once created, the verification request has a short lifespan; in the demo, it was valid for 5 minutes.
- Only Pop knows that the verification request is linked to `publius`.

---

We now need to transfer this verification request to CheckMyAge. How can we do that?

- Since CheckMyAge stored a copy of that request, we only need to transfer the request ID.
- Pop can open CheckMyAge in a new window in John Smith's browser; the URL will include the request ID.

Here's a sample URL: `https://www.checkmyage.com/verify/7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk`

Notes:

- There are also ways to make this work with a mobile app.
- Could people share this URL, similar to how they shared an age certificate?
    - In short, this problem is easier to mitigate, though the explanation is a bit more technical.
    - The verification request is also time-limited; this also will serve as a mitigation.

---

Next, let's track this verification request on CheckMyAge.

- CheckMyAge uses the request ID in the URL to retrieve the full verification request.
- CheckMyAge checks that the verification request is not expired.
- Once John Smith logs in to CheckMyAge, CheckMyAge links the verification request to John Smith.
- CheckMyAge confirms that John Smith wants to verify an account on Pop.
- CheckMyAge creates an age certificate with two pieces of information:
    - the verification request
        - Request ID: `7-DQzGbLlaMeQkwMWE_AGDXgFnbWfRR_P0UkwYEGbZk`
        - Site: Pop
        - Expiration: July 25, 2023, 12:47:56 PM MDT
    - the user data for John Smith (with the age anonymized into an age range)
        - ID: `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4`
        - Age: 18+
- CheckMyAge digitally signs the age certificate and securely transmits it to Pop.

Notes:

- Only CheckMyAge knows that the verification request is linked to John Smith.
- After it transmits the age certificate to Pop, CheckMyAge should not retain the age certificate.
- CheckMyAge also should not retain any records of where users have verified their age.

---

What does Pop do when it receives the age certificate? Recall that it linked the verification request to `publius`.

- Pop verifies the signature, recipient, and expiration on the age certificate.
- Pop matches the verification request in the age certificate to `publius`.
- Pop extracts the user data from the age certificate:
    - ID: `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4`
    - Age: 18+
- Pop checks that no other accounts have the same ID (`uhzmISXl...`).
- Pop stores this user data for `publius`. `publius` is now verified!

Notes:

- Once age verification is complete, Pop should not retain the age certificate.
- John Smith never sees the age certificate; you can't phish for something he does not have.

**Tradeoff: Knowing the Recipient of an Age Certificate**

Previously, CheckMyAge did not know who the recipient of an age certificate was.
Now, it knows the recipient of an age certificate.

As we stipulated earlier, CheckMyAge should not retain any records of where users have verified their age;
retaining such records introduces a de-anonymization risk.
CheckMyAge can include this provision in its privacy policy; a federal/state law could also mandate this.
(Interestingly enough, the proposed [federal age verification bill][federal-avs] includes a pilot program
for a federal age verification service. One requirement is that the pilot program should
"[k]eep no records of the social media platforms where users have verified their identity.")

Once you add in the aforementioned stipulation, though, this is a great tradeoff&mdash;especially
when you consider the extra privacy and security benefits that come with this version.

**[Technical] Solution: Mobile Apps**

What if CheckMyAge has a mobile app? When Pop redirects John Smith to a URL for CheckMyAge,
developers can use App Links (Android) or Universal Links (iPhone) to open that URL in the app instead of the browser.
(If John Smith does not have the CheckMyAge app, the URL would open in a browser as usual.)

**[Technical] Solution: TLS 1.3**

When CheckMyAge securely transmits an age certificate to Pop, it would ideally use TLS 1.3.
TLS 1.3 provides forward secrecy; even if a hacker stole Pop's private key,
it could not decrypt past communications between CheckMyAge and Pop.

**[Technical] Solution: CSRF**

CheckMyAge should also protect against cross-site request forgery (CSRF) attacks.
When CheckMyAge confirms that John Smith wants to verify an account on Pop, it should use a CSRF token.

**[Technical] Solution: Sharing URLs**

When CheckMyAge receives an HTTP request with a verification request ID in the URL,
it could create a token for that request ID, and store that token in a first-party cookie as part of the HTTP response.
(On subsequent HTTP requests with the same request ID, it will not generate a token.)
Without that token, it will not be possible to create an age certificate.

Now, if John Smith shares that URL with Bobby Tables, the URL won't work for Bobby Tables in his browser.
Of course, there are ways to work around this, but if you have to, e.g., use a browser's developer tools&mdash;as
opposed to copy-and-pasting a URL&mdash;fewer people will use that workaround.

There probably are other possible mitigations as well, though that is beyond the scope of a proof-of-concept.

### v5: Different IDs for Different Sites

**Problem: Data Breaches**

What if a data breach occurs on CheckMyAge, revealing the IDs of all its users? Now, we can link a real name to an ID.
Moreover, the same ID is used on every social media site. That's a severe de-anonymization risk.

**Solution: ID Transformation**

What if the ID on the age certificate is different than the one stored on CheckMyAge?

Through some cryptographic magic, CheckMyAge can generate a secret key, and use it to transform the ID:

- It is impossible to figure out the new ID from the original ID, unless you know the secret key.
- It is impossible to figure out the original ID from the new ID (even if you know the secret key).

Moreover, since sites now have to register which CheckMyAge, as part of that registration process,
CheckMyAge can generate a secret key for each site. (This key is not shared with the site.)
Since each site has a different key, this transformation produces a different ID for each site.

---

A couple of examples may help here.

Creating an age certificate for Crackle:

- John Smith's ID on CheckMyAge is `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4`.
- CheckMyAge's secret key for Crackle is `pER-dDPdsvdvcP9szpckd6GHHc1qg44Rt70LTUqHTpY`.
- John Smith's ID and CheckMyAge's key are used create a new ID: `keXeY3kiQDgOhenFw9GMFv3zUFSCSsqrcsmwf3DvpdA`.

Creating an age certificate for Pop:

- John Smith's ID on CheckMyAge is `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4`.
- CheckMyAge's secret key for Pop is `W1zah29NMWEOEsd8VNFX6E3Vo8Z-HLNQ5cDH3-9KyVg`.
- John Smith's ID and CheckMyAge's key are used create a new ID: `iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4`.

---

Now, CheckMyAge, Crackle, and Pop each store a different ID.

This solution has some strong security guarantees:

- To figure out that John Smith is `publius` on Pop, you would need to...
    - steal John Smith's real name and ID from CheckMyAge via a data breach, and
    - steal CheckMyAge's secret key for Pop (i.e., a key breach).
- By itself, stealing `publius`'s ID from Pop does not help.
    - You cannot reverse this transformation to obtain the corresponding ID for CheckMyAge&mdash;even
      if you know CheckMyAge's secret key for Pop.

**[Technical] Solution: ID Transformation, Continued**

Let's recall the requirements for this transformation:

- It is impossible to figure out the new ID from the original ID, unless you know the secret key.
- It is impossible to figure out the original ID from the new ID (even if you know the secret key).

A hash function has the latter property, but what has both properties? An HMAC.

While we do not need a message authentication code (MAC), we do need one of the security properties of an HMAC:
it is impossible to compute the HMAC of a message if you do not know the secret key.
Ergo, if the new ID is the HMAC of the original ID, then it is impossible to compute the new ID from the original ID
if you do not know the secret key.

If an HMAC-SHA256 is used, the new ID will also conveniently have the same number of bits as the original ID.

**Tradeoff: Changing Keys**

If you store any long-term keys, you should change them at a regular but infrequent interval;
rotating in a new key once a year is a common option.

However, since the new ID depends on the original ID and the key, if you change the key, you also change the new ID.
Thus, whenever the key for Pop changes, Pop will need to make every account re-verify to get an updated ID&mdash;though
it can establish a grace period of, e.g., one week.

Since this is a very infrequent occurrence, that tradeoff is fine.

**Other Consideration: Surreptitious Forwarding**

What if somebody tries to take his age certificate for Crackle, and use it for Pop instead?
If Crackle decides to go rogue, this becomes a legitimate concern.
And since a person now has a different ID on each site, one person could verify multiple accounts on Pop:
one using his age certificate for Pop, and another using his age certificate for Crackle.

Of course, there's an easy fix here: put the recipient on the age certificate. (We already do this.)
Pop will reject an age certificate if it sees that the recipient is Crackle.

This is a specific case of a more general security principle: you should not digitally sign a document
if the document does not say who the recipient is. Otherwise, tricksters can gain someone's trust
by saying that you signed the document (which is true), but they could lie about who is the recipient of said document.

### v6: Local Security Measures for a Site

**Problem: Single Point of Failure**

On the micro level, there probably is not a single point of failure; stealing a key would be very difficult,
and multiple things would have to go wrong for that to happen. On the macro level, there is a single point of failure:
the security of this solution depends entirely on CheckMyAge.

We could also use a layer of security that Pop controls.
This problem can be viewed as both a technical problem and a political problem:

- Technically speaking, defense-in-depth is good. Another layer of security
  that is controlled by a different entity would certainly help.
- Politically speaking, if Pop wants to lobby against an age verification law, it will argue
  that CheckMyAge (or any third-party age verification service) is untrustworthy&mdash;and
  that it opposes age verification because it genuinely cares about the privacy of its users.

However, if there is a layer of security that is entirely controlled by Pop,
that political argument would land very differently. Since Pop genuinely cares about the privacy of its users,
it would never let the security of its own systems be compromised. Thus, the anonymity of Pop's users will be protected.

**Problem: Hiding IDs**

When CheckMyAge generates an age certificate for Pop, it does see the ID that will be stored on Pop.
(That ID is quickly forgotten, though; CheckMyAge never stores it.)

Ideally, CheckMyAge should not see the ID that is stored on Pop&mdash;though
this is pretty far down the list of problems.

**Solution: Local ID Transformation**

What if we applied the same ID transformation locally on Pop?

When Pop receives an age certificate, it will not store the IDs on that certificate.
Instead, Pop will have its own secret key, and it will use that key to transform the IDs.

Now, the ID on the age certificate becomes an ephemeral, intermediate ID.
CheckMyAge does not see the ID stored on Pop, and Pop does not see the ID stored on CheckMyAge.

---

As an example, here is the chain of IDs that are used to verify `publius` on Pop:

- John Smith's ID on CheckMyAge: `uhzmISXl7szUDLVuYNvDVf6jiL3ExwCybtg-KlazHU4`.
- ID on the age certificate: `iaDG-BXou0kKr5gg2j0BJj0RKsa00bVvnpbRCiEism4`.
    - This ID is created from the ID stored on CheckMyAge (`uhzmISXl...`) and CheckMyAge's secret key for Pop.
- `publius`'s ID on Pop: `Uum6yHO7tgND6ffCHsidSpghQz8Eq7PlkmWHzkVL2DE`
    - This ID is created from the ID on the age certificate (`iaDG-BXo...`) and Pop's secret key.

---

To de-anonymize `publius`, you would need to steal a key from both CheckMyAge and Pop.
De-anonymizing Publius back in the 18th century would probably be a much easier task.

## Postscript

**"But what about this problem?"** Before you point out any problems, you should ask yourself these questions:

1. What is the practical impact of this problem? (E.g., what percentage of kids can bypass this system? 0.5%? 50%?)
2. Are there ways to fix or mitigate this problem?
3. If this problem cannot be fixed, is it a fatal flaw, or is it a minor issue?

In the detailed design, we did a pretty good job of spotting problems,
but we did an even better job of finding ways to fix or mitigate those problems.
(And I'm certainly not the only one capable of doing that; others may also find ways to improve this design.)
While this proof-of-concept solution is not perfect, it is certainly good enough to prove that the concept is possible.

By contrast, a common problem with the "expert analysis" here is that it lists problems with age verification,
and then it jumps to an (often predetermined) conclusion that age verification and privacy cannot coexist.
Such analysis does not make a serious effort to answer those three questions.

---

Privacy-conscious age verification for social media can be done.
We need to stop listening to the experts who [write six-part series][rstreet-series] on how it cannot be done,
and start listening to the experts who find six ways to make it better.

[rstreet-dne]: https://www.rstreet.org/commentary/the-technology-to-verify-your-age-without-violating-your-privacy-does-not-exist/
[anonymous-1a]: https://www.mtsu.edu/first-amendment/article/32/anonymous-speech
[secure-random]: https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/security/SecureRandom.html
[federal-avs]: https://www.congress.gov/bill/118th-congress/senate-bill/1291/text#id3801b799928248d18e235e369f524988
[rstreet-series]: https://www.rstreet.org/commentary/the-fundamental-problems-with-social-media-age-verification-legislation/
