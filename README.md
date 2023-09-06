# Proof-of-Concept: Privacy-Conscious Age Verification

*(To take a quick look, you can look at the overview and the demo.)*

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
3. They ensure that one person can only verify one account for each site.

Each person also has a different ID for CheckMyAge, Crackle, and Pop:

- If both CheckMyAge and Pop suffered data breaches, you could not figure out that `publius` is John Smith.
- If both Crackle and Pop suffered data breaches,
  you could not figure out that `JohnS` and `publius` are the same person.

(If one engineer could accomplish this, imagine what a team of engineers with even more expertise could accomplish.)

## Design Principles (and Extended Demo)

The design is based on three keys:

- a key consideration: protecting the anonymity of users
- a key problem: maintaining the chain of custody for a digital age certificate
- a key insight: that we can build a good enough solution if we view this as deduplication problem

### Key Consideration

Many social media accounts are anonymous. Preserving their anonymity is key&mdash;especially
because anonymous speech does have some protection under the First Amendment.

- For protecting the anonymity of users, we want an "A" grade.
- For stopping kids from bypassing the system, we want a "B/C" grade.

That "B/C" grade is certainly not a ceiling, though; these grades are primarily used to express tradeoffs.

### Key Problem

Let’s say that John Smith uses a digital age certificate to verify his social media account:

1. How do we verify that the person who requested a digital age certificate is John Smith?
2. How do we verify that the person who used that age certificate is John Smith?
3. How do we verify that John Smith used that age certificate for his own social media account?

A lot of the focus is on the first question, but we need to consider the entire chain of custody.
For example, even if John Smith verified his identity before he received an age certificate,
that means little if he can share that age certificate with someone else&mdash;and nothing stops them from using it.

### Key Insight

What if we viewed this problem as a deduplication problem?
How can we ensure that John Smith can only verify one account for each site?

Even when we cannot fully guarantee the chain of custody, if one person can only verify one account,
that means that they cannot verify both their own account and someone else’s account.
(At most, if they did not have an account of their own, they could verify one and only one other person’s account.)

The deduplication problem is easier to solve in a privacy-conscious way,
and it is a good enough solution to earn that "B/C" grade for effectiveness.

(From there, we can pursue more incremental improvements that bump up that grade for effectiveness.
The goal here is not to make it impossible to bypass the system; the goal is simply to make it harder.)

### Extended Demo

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

To dive deeper into this proof-of-concept, you can read the [design](DESIGN.md).

[rstreet-dne]: https://www.rstreet.org/commentary/the-technology-to-verify-your-age-without-violating-your-privacy-does-not-exist/
