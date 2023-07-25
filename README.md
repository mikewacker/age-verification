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
- [#1] CheckMyAge stores the verification request.
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
- CheckMyAge asks Bobby Smith if he wants to verify an account on Pop.
- Bobby Smith confirms that he wants to verify an account on Pop.
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

A key consideration for age verification is protecting the anonymity of social media accounts. Here are the goals:

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

That statement is technically true, but practically useless. Is it a mere 0.5% of kids that bypass age verification,
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

However, there is some nuance to this conversation. In short, there are two types of breaches to consider:

- data breach: a breach that exposes user data
- key breach: a breach that exposes secret keys for your site

(E.g., here is a mostly correct explanation of what happens when you visit `https://www.amazon.com`;
the `s` in `https` stands for secure. Your web browser will use Amazon's public key
to encrypt any data that is sent to Amazon. To decrypt that data,
you would need to use Amazon's private key&mdash;which only Amazon knows.)

Data breaches are fare more common than key breaches, and most cybersecurity incidents in the news are data breaches.
We will mostly focus on mitigating the impact of a data breach, though we will consider both types of breaches.

[rstreet-dne]: https://www.rstreet.org/commentary/the-technology-to-verify-your-age-without-violating-your-privacy-does-not-exist/
[anonymous-1a]: https://www.mtsu.edu/first-amendment/article/32/anonymous-speech
